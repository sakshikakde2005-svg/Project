import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private final int port;
    // roomName -> set of ClientHandler
    private final Map<String, Set<ClientHandler>> rooms = new ConcurrentHashMap<>();
    // username -> handler
    private final Map<String, ClientHandler> users = new ConcurrentHashMap<>();

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Chat server started on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            ClientHandler handler = new ClientHandler(this, clientSocket);
            new Thread(handler).start();
        }
    }

    // registration
    boolean addUser(String username, ClientHandler handler) {
        if (username == null || username.trim().isEmpty()) return false;
        return users.putIfAbsent(username, handler) == null;
    }

    void removeUser(String username) {
        ClientHandler ch = users.remove(username);
        if (ch != null) {
            leaveAllRooms(ch);
        }
    }

    void createRoomIfAbsent(String room) {
        rooms.computeIfAbsent(room, r -> ConcurrentHashMap.newKeySet());
    }

    void joinRoom(String room, ClientHandler ch) {
        createRoomIfAbsent(room);
        Set<ClientHandler> set = rooms.get(room);
        set.add(ch);
        ch.joinedRooms.add(room);
        broadcastToRoom(room, "[Server] " + ch.username + " joined the room.");
    }

    void leaveRoom(String room, ClientHandler ch) {
        Set<ClientHandler> set = rooms.get(room);
        if (set != null) {
            set.remove(ch);
            ch.joinedRooms.remove(room);
            broadcastToRoom(room, "[Server] " + ch.username + " left the room.");
            if (set.isEmpty()) {
                rooms.remove(room);
            }
        }
    }

    void leaveAllRooms(ClientHandler ch) {
        for (String r : new ArrayList<>(ch.joinedRooms)) {
            leaveRoom(r, ch);
        }
    }

    void broadcastToRoom(String room, String message) {
        Set<ClientHandler> set = rooms.get(room);
        if (set != null) {
            for (ClientHandler ch : set) {
                ch.send(message);
            }
        }
    }

    void sendPrivate(String toUser, String message, String fromUser) {
        ClientHandler target = users.get(toUser);
        if (target != null) {
            target.send("[PM from " + fromUser + "] " + message);
        }
    }

    List<String> listRooms() {
        return new ArrayList<>(rooms.keySet());
    }

    List<String> listUsersInRoom(String room) {
        Set<ClientHandler> set = rooms.get(room);
        if (set == null) return Collections.emptyList();
        List<String> names = new ArrayList<>();
        for (ClientHandler ch : set) names.add(ch.username);
        return names;
    }

    public static void main(String[] args) throws IOException {
        int port = 5000;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        new ChatServer(port).start();
    }

    // Inner class
    static class ClientHandler implements Runnable {
        private final ChatServer server;
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        String username;
        final Set<String> joinedRooms = ConcurrentHashMap.newKeySet();

        ClientHandler(ChatServer server, Socket socket) {
            this.server = server;
            this.socket = socket;
        }

        public void run() {
            try {
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

                out.println("Welcome! Enter your username:");
                username = in.readLine();
                if (username == null) closeConnection();
                username = username.trim();

                if (!server.addUser(username, this)) {
                    out.println("Username already taken. Connection closing.");
                    closeConnection();
                    return;
                }

                out.println("Hello " + username + "! Use /help for commands.");
                System.out.println(username + " connected from " + socket.getInetAddress());
                // default join lobby
                server.joinRoom("lobby", this);

                String line;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    if (line.startsWith("/")) {
                        handleCommand(line);
                    } else {
                        // broadcast to all joined rooms
                        for (String room : joinedRooms) {
                            server.broadcastToRoom(room, "[" + room + "] " + username + ": " + line);
                        }
                    }
                }
            } catch (IOException e) {
                // e.printStackTrace();
            } finally {
                closeConnection();
            }
        }

        void handleCommand(String cmdLine) {
            String[] parts = cmdLine.split(" ", 3);
            String cmd = parts[0].toLowerCase();

            try {
                switch (cmd) {
                    case "/help":
                        send("Commands:\n"
                                + "/join <room>       - join or create a room\n"
                                + "/leave <room>      - leave a room\n"
                                + "/rooms             - list active rooms\n"
                                + "/users <room>      - list users in a room\n"
                                + "/msg <user> <text> - private message to user\n"
                                + "/quit              - disconnect\n"
                                + "Type plain text to send to all rooms you are in.");
                        break;

                    case "/join":
                        if (parts.length < 2) {
                            send("Usage: /join <room>");
                        } else {
                            String room = parts[1];
                            server.joinRoom(room, this);
                        }
                        break;

                    case "/leave":
                        if (parts.length < 2) {
                            send("Usage: /leave <room>");
                        } else {
                            String room = parts[1];
                            server.leaveRoom(room, this);
                        }
                        break;

                    case "/rooms":
                        List<String> r = server.listRooms();
                        send("Rooms: " + (r.isEmpty() ? "(none)" : String.join(", ", r)));
                        break;

                    case "/users":
                        if (parts.length < 2) {
                            send("Usage: /users <room>");
                        } else {
                            List<String> u = server.listUsersInRoom(parts[1]);
                            send("Users in " + parts[1] + ": " + (u.isEmpty() ? "(none)" : String.join(", ", u)));
                        }
                        break;

                    case "/msg":
                        if (parts.length < 3) {
                            send("Usage: /msg <user> <message>");
                        } else {
                            String target = parts[1];
                            String msg = parts[2];
                            server.sendPrivate(target, msg, username);
                            send("[PM to " + target + "] " + msg);
                        }
                        break;

                    case "/quit":
                        send("Goodbye!");
                        closeConnection();
                        break;

                    default:
                        send("Unknown command. Use /help for list.");
                }
            } catch (Exception e) {
                send("Error processing command: " + e.getMessage());
            }
        }

        void send(String message) {
            if (out != null) out.println(message);
        }

        void closeConnection() {
            try {
                if (username != null) {
                    System.out.println(username + " disconnected.");
                    server.removeUser(username);
                }
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}