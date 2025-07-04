package server.Database;

import shared.LegacyCore.ChessGame;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerDatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:PlayerDB.db";
    private Connection connection;

    static {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found. Please add sqlite-jdbc.jar to your classpath.");
            e.printStackTrace();
        }
    }

    public PlayerDatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            // Create a connection to the database
            connection = DriverManager.getConnection(DB_URL);
            // Enable foreign keys
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
            createTables();
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Create games table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS players (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Create moves table
        }
    }

    public void register(String username, String password) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not initialized");
        }

        String sql = """
            INSERT INTO players (username, password)
            VALUES (?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            pstmt.executeUpdate();

            // Get the generated game ID
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid();")) {
                if (rs.next()) {
                    int gameId = rs.getInt(1);
//                    saveMoves(gameId, game.moves());
                }
            }
        }
    }



    public String getUser(String username, String password) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not initialized");
        }

        String sql = "SELECT * FROM players WHERE username = ? ORDER BY created_at DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String uname = rs.getString("username");
                    String pass = rs.getString("password");
                    String createdAt = rs.getString("created_at");
                    // Add other fields as needed
                    if(pass.equals(password)) {
                        return uname;
                    }else{
                        return null;
                    }

                }
            }
        }

        return null;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}