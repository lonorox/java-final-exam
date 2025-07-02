package server.Database;

import shared.LegacyCore.ChessGame;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS moves (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    game_id INTEGER,
                    move_number INTEGER,
                    white_move TEXT,
                    black_move TEXT,
                    FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE
                )
            """);
        }
    }
    public void clearGamesTable() throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not initialized");
        }

        String sql = "DELETE FROM games";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
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
                    saveMoves(gameId, game.moves());
                }
            }
        }
    }

    private void saveMoves(int gameId, String moves) throws SQLException {
        if (connection == null) {
            throw new SQLException("Database connection is not initialized");
        }

        String[] movePairs = moves.split("\\d+\\.");
        String sql = "INSERT INTO moves (game_id, move_number, white_move, black_move) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 1; i < movePairs.length; i++) {
                String[] movesInPair = movePairs[i].trim().split("\\s+");
                pstmt.setInt(1, gameId);
                pstmt.setInt(2, i);
                pstmt.setString(3, movesInPair[0]);
                pstmt.setString(4, movesInPair.length > 1 ? movesInPair[1] : null);
                pstmt.executeUpdate();
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
} 