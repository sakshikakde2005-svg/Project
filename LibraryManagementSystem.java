import java.sql.*;
import java.util.Scanner;

public class LibraryManagementSystem {

    private static final String DB_URL = "jdbc:sqlite:library.db";

    public static void main(String[] args) {
        createTables();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== Library Management System =====");
            System.out.println("1. Add User");
            System.out.println("2. Add Book");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. View All Books");
            System.out.println("6. View All Users");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> addUser(sc);
                case 2 -> addBook(sc);
                case 3 -> issueBook(sc);
                case 4 -> returnBook(sc);
                case 5 -> viewBooks();
                case 6 -> viewUsers();
                case 7 -> {
                    System.out.println("Exiting...");
                    sc.close();
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // Create SQLite tables
    private static void createTables() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String userTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "email TEXT UNIQUE NOT NULL)";
            stmt.execute(userTable);

            String bookTable = "CREATE TABLE IF NOT EXISTS books (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT NOT NULL," +
                    "author TEXT NOT NULL," +
                    "isIssued INTEGER DEFAULT 0)";
            stmt.execute(bookTable);

            System.out.println("Database ready.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a new user
    private static void addUser(Scanner sc) {
        System.out.print("Enter user name: ");
        sc.nextLine();
        String name = sc.nextLine();
        System.out.print("Enter email: ");
        String email = sc.nextLine();

        String sql = "INSERT INTO users(name, email) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:1ibrary.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
            System.out.println("User added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
    }

    // Add a new book
    private static void addBook(Scanner sc) {
        System.out.print("Enter book title: ");
        sc.nextLine();
        String title = sc.nextLine();
        System.out.print("Enter author name: ");
        String author = sc.nextLine();

        String sql = "INSERT INTO books(title, author) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.executeUpdate();
            System.out.println("Book added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    // Issue a book
    private static void issueBook(Scanner sc) {
        System.out.print("Enter book ID to issue: ");
        int bookId = sc.nextInt();

        String sql = "UPDATE books SET isIssued = 1 WHERE id = ? AND isIssued = 0";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:1 ibrary.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Book issued successfully!");
            } else {
                System.out.println("Book not available or already issued.");
            }
        } catch (SQLException e) {
            System.out.println("Error issuing book: " + e.getMessage());
        }
    }

    // Return a book
    private static void returnBook(Scanner sc) {
        System.out.print("Enter book ID to return: ");
        int bookId = sc.nextInt();

        String sql = "UPDATE books SET isIssued = 0 WHERE id = ? AND isIssued = 1";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Book returned successfully!");
            } else {
                System.out.println("Book not found or not issued.");
            }
        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
    }

    // View all books
    private static void viewBooks() {
        String sql = "SELECT * FROM books";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- Book List ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        " | Title: " + rs.getString("title") +
                        " | Author: " + rs.getString("author") +
                        " | Issued: " + (rs.getInt("isIssued") == 1 ? "Yes" : "No"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // View all users
    private static void viewUsers() {
        String sql = "SELECT * FROM users";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- User List ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        " | Name: " + rs.getString("name") +
                        " | Email: " + rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}