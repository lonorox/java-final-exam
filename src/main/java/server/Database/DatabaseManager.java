package server.Database;

import server.ChessServer;
import shared.LegacyCore.ChessGame;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:GamesDB.db";
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

    public DatabaseManager() {
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
                CREATE TABLE IF NOT EXISTS games (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    event TEXT,
                    site TEXT,
                    date TEXT,
                    round TEXT,
                    white TEXT,
                    black TEXT,
                    result TEXT,
                    pgn TEXT,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Create moves table
        }
    }
    public void clearGamesTable() throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not initialized");
        }

        String sql = "DELETE FROM games";
        String sql2 = "DELETE FROM sqlite_sequence WHERE name='games'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
        try (PreparedStatement stmt = connection.prepareStatement(sql2)) {
            stmt.executeUpdate();
        }
    }

    public void saveGame(ChessGame game) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not initialized");
        }

        String sql = """
            INSERT INTO games (event, site, date, round, white, black, result, pgn)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, game.tags().getOrDefault("Event", ""));
            pstmt.setString(2, game.tags().getOrDefault("Site", ""));
            pstmt.setString(3, game.tags().getOrDefault("Date", ""));
            pstmt.setString(4, game.tags().getOrDefault("Round", ""));
            pstmt.setString(5, game.tags().getOrDefault("White", ""));
            pstmt.setString(6, game.tags().getOrDefault("Black", ""));
            pstmt.setString(7, game.tags().getOrDefault("Result", "*"));
            pstmt.setString(8, game.moves());

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

    public List<ChessGame> loadGames() throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not initialized");
        }

        List<ChessGame> games = new ArrayList<>();
        String sql = "SELECT * FROM games ORDER BY created_at DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Create a map of tags
                java.util.Map<String, String> tags = new java.util.HashMap<>();
                tags.put("Event", rs.getString("event"));
                tags.put("Site", rs.getString("site"));
                tags.put("Date", rs.getString("date"));
                tags.put("Round", rs.getString("round"));
                tags.put("White", rs.getString("white"));
                tags.put("Black", rs.getString("black"));
                tags.put("Result", rs.getString("result"));

                // Create ChessGame object
                ChessGame game = new ChessGame(tags, rs.getString("pgn"));
                games.add(game);
            }
        }

        return games;
    }
    public List<ChessGame> loadOnlineGames() throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not initialized");
        }

        List<ChessGame> games = new ArrayList<>();
        String sql = "SELECT * FROM games WHERE event = 'Online Game' ORDER BY created_at DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, String> tags = new HashMap<>();
                tags.put("Event", rs.getString("event"));
                tags.put("Site", rs.getString("site"));
                tags.put("Date", rs.getString("date"));
                tags.put("Round", rs.getString("round"));
                tags.put("White", rs.getString("white"));
                tags.put("Black", rs.getString("black"));
                tags.put("Result", rs.getString("result"));

                ChessGame game = new ChessGame(tags, rs.getString("pgn"));
                games.add(game);
            }
        }
        return games;
    }
    public String exportGameToPGN(int gameId) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not initialized");
        }

        String sql = "SELECT * FROM games WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, gameId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                StringBuilder pgn = new StringBuilder();
                
                // Add tags
                pgn.append("[Event \"").append(rs.getString("event")).append("\"]\n");
                pgn.append("[Site \"").append(rs.getString("site")).append("\"]\n");
                pgn.append("[Date \"").append(rs.getString("date")).append("\"]\n");
                pgn.append("[Round \"").append(rs.getString("round")).append("\"]\n");
                pgn.append("[White \"").append(rs.getString("white")).append("\"]\n");
                pgn.append("[Black \"").append(rs.getString("black")).append("\"]\n");
                pgn.append("[Result \"").append(rs.getString("result")).append("\"]\n\n");
                
                // Add moves
                pgn.append(rs.getString("pgn"));
                
                return pgn.toString();
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
    public void saveGameToDatabase(String result, String whitePlayerName, String BlackPlayerName,List<String> moves) throws SQLException {
        try {
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            String formatted = today.format(formatter);

            Map<String, String> tags = new HashMap<>();
            tags.put("Event", "Online Game");
            tags.put("Site", "KIU_WIFI");
            tags.put("Date",formatted);
            tags.put("White", whitePlayerName);
            tags.put("Black", BlackPlayerName);
            tags.put("Result", result);

            StringBuilder pgn = new StringBuilder();
            for (int i = 0; i < moves.size(); i++) {
                if (i % 2 == 0) {
                    pgn.append((i/2 + 1) + ".").append(moves.get(i)).append(" ");
                } else {
                    pgn.append(moves.get(i)).append(" ");
                }
            }

            ChessGame game = new ChessGame(tags, pgn.toString().trim());
            ChessServer.getDbManager().saveGame(game);
        } catch (Exception e) {
            System.err.println("Failed to save game to database: " + e.getMessage());
        }
    }
} 