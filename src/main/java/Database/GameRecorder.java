package Database;

import LegacyCore.ChessGame;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class GameRecorder {
    private final DatabaseManager dbManager;
    private Map<String, String> currentGameTags;
    private StringBuilder currentGameMoves;
    private int currentGameId;

    public GameRecorder() {
        this.dbManager = new DatabaseManager();
        this.currentGameTags = new HashMap<>();
        this.currentGameMoves = new StringBuilder();
    }

    public void startNewGame(String white, String black) {
        currentGameTags = new HashMap<>();
        currentGameMoves = new StringBuilder();
        
        // Set default tags
        currentGameTags.put("Event", "Casual Game");
        currentGameTags.put("Site", "Local");
        currentGameTags.put("Date", LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        currentGameTags.put("Round", "1");
        currentGameTags.put("White", white);
        currentGameTags.put("Black", black);
        currentGameTags.put("Result", "*");
    }

    public void recordMove(int moveNumber, String whiteMove, String blackMove) {
        currentGameMoves.append(moveNumber).append(". ")
                       .append(whiteMove).append(" ");
        
        if (blackMove != null && !blackMove.isEmpty()) {
            currentGameMoves.append(blackMove).append(" ");
        }
    }

    public void endGame(String result) {
        currentGameTags.put("Result", result);
        
        // Create and save the game
        ChessGame game = new ChessGame(currentGameTags, currentGameMoves.toString().trim());
        try {
            dbManager.saveGame(game);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String exportGameToPGN(int gameId) {
        try {
            return dbManager.exportGameToPGN(gameId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void importGamesFromPGN(String pgnContent) {
        // Use your existing PGNReader to parse the games
        // and save them to the database
        // This will be implemented when integrating with your PGN parser
    }

    public void close() {
        dbManager.close();
    }
} 