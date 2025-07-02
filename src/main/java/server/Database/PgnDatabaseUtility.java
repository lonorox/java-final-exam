package server.Database;

import shared.LegacyCore.ChessGame;
import shared.PgnAnalyzers.FileHandler;
import shared.PgnAnalyzers.PGNReader;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;


public class PgnDatabaseUtility {
    private final DatabaseManager dbManager;

    public PgnDatabaseUtility() {
        this.dbManager = new DatabaseManager();
    }
    public void deleteDb(){
        try{
            dbManager.clearGamesTable();
        } catch (Exception e) {
            System.err.println("Error importing PGN file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Import PGN file(s) into database
    public void importPgnToDatabase(String pgnFilePath) {
        try {
            // Read PGN file using your existing PGNReader
            BufferedReader reader = FileHandler.readPgn(pgnFilePath);
            PGNReader pgnReader = new PGNReader();
            pgnReader.extractGames(reader);

            // Save each game to database
            System.out.println(pgnReader.getGames().size());
            for (ChessGame game : pgnReader.getGames()) {
                System.out.println(game.tags().get("Black"));
                dbManager.saveGame(game);
//                System.out.println(game.tags());
//                System.out.println(game.moves());
            }
            System.out.println("Successfully imported games from: " + pgnFilePath);
        } catch (Exception e) {
            System.err.println("Error importing PGN file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Export all games from database to a single PGN file
    public void exportDatabaseToPgn(String outputFilePath) {
        try {
            List<ChessGame> games = dbManager.loadGames();
            StringBuilder pgnContent = new StringBuilder();

            for (ChessGame game : games) {
                // Add tags
                for (Map.Entry<String, String> tag : game.tags().entrySet()) {
                    pgnContent.append("[").append(tag.getKey()).append(" ")
                             .append(tag.getValue()).append("]\n");
                }
                pgnContent.append("\n");
                
                // Add moves
                pgnContent.append(game.moves()).append("\n\n");
            }

            // Write to file
            Files.write(Paths.get(outputFilePath), pgnContent.toString().getBytes());
            System.out.println("Successfully exported games to: " + outputFilePath);
        } catch (Exception e) {
            System.err.println("Error exporting to PGN file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Export a single game from database to PGN
    public void exportGameToPgn(int gameId, String outputFilePath) {
        try {
            String pgn = dbManager.exportGameToPGN(gameId);
            if (pgn != null) {
                Files.write(Paths.get(outputFilePath), pgn.getBytes());
                System.out.println("Successfully exported game " + gameId + " to: " + outputFilePath);
            } else {
                System.err.println("Game " + gameId + " not found in database");
            }
        } catch (Exception e) {
            System.err.println("Error exporting game to PGN file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void close() {
        dbManager.close();
    }

    // Example usage
}