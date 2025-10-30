import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class ChatClient {
    private final String host;
    private final int port;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private final BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

        // Thread to read server messages
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.submit(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                // connection closed
            } finally {
                System.out.println("Connection closed by server.");
                System.exit(0);
            }
        });

        // Read console input and send to server
        String userLine;
        while ((userLine = console.readLine()) != null) {
            out.println(userLine);
            if (userLine.trim().equalsIgnoreCase("/quit")) break;
        }

        close();
    }

    private void close() {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            // ignore
        }
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;
        if (args.length >= 1) host = args[0];
        if (args.length >= 2) port = Integer.parseInt(args[1]);

        try {
            new ChatClient(host, port).start();
        } catch (IOException e) {
            System.err.println("Could not connect: " + e.getMessage());
        }
    }
}