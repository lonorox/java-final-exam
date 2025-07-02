package LegacyGUI;

import server.Database.DatabaseManager;
import shared.LegacyCore.ChessGame;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Handles operations related to chess games stored in the database.
 * Provides functionality for loading and managing games from the database.
 */
public class DatabaseGameHandler {
    private final JFrame parentFrame;

    /**
     * Creates a new DatabaseGameHandler.
     * @param parentFrame The parent frame for dialogs
     */
    public DatabaseGameHandler(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    /**
     * Opens the database game selection dialog and handles the selected game.
     */
    public void openGameFromDatabase() {
        try {
            DatabaseManager dbManager = new DatabaseManager();
            List<ChessGame> dbGames = dbManager.loadGames();
            
            if (dbGames == null || dbGames.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "No games found in database.");
                return;
            }

            String[] gameOptions = prepareGameOptions(dbGames);
            String selected = showGameSelectionDialog(gameOptions);
            
            if (selected != null) {
                int selectedIndex = -1;
                for (int i = 0; i < gameOptions.length; i++) {
                    if (gameOptions[i].equals(selected)) {
                        selectedIndex = i;
                        break;
                    }
                }

                if (selectedIndex != -1) {
                    ChessGame chosenGame = dbGames.get(selectedIndex);
                    System.out.println("Selected DB game: " + gameOptions[selectedIndex]);
                    System.out.println("Tags: " + chosenGame.tags());
                    System.out.println("Moves: " + chosenGame.moves());

                    int review = JOptionPane.showConfirmDialog(
                            parentFrame,
                            "Review this game?",
                            "Review",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (review == JOptionPane.YES_OPTION) {
                        GameReviewManager reviewManager = new GameReviewManager(parentFrame);
                        reviewManager.reviewGame(chosenGame);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame, "Error loading games from DB: " + ex.getMessage());
        }
    }

    public void exportGameToPGN() {
        try {
            DatabaseManager dbManager = new DatabaseManager();
            List<ChessGame> dbGames = dbManager.loadOnlineGames();

            if (dbGames == null || dbGames.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "No games found in database.");
                return;
            }

            String[] gameOptions = prepareGameOptions(dbGames);
            String selected = showGameSelectionDialog(gameOptions);

            if (selected != null) {
                int selectedIndex = -1;
                for (int i = 0; i < gameOptions.length; i++) {
                    if (gameOptions[i].equals(selected)) {
                        selectedIndex = i;
                        break;
                    }
                }
                if (selectedIndex != -1) {
                    System.out.println(selectedIndex);
                    StringBuilder pgn = new StringBuilder();
                    ChessGame chosenGame = dbGames.get(selectedIndex);
                    String[] tagOrder = {"Event", "Site", "Date", "Round", "White", "Black", "Result"};
                    // Add tags
                    for (String tag : tagOrder) {
                        String value = chosenGame.tags().getOrDefault(tag, "");
                        pgn.append("[").append(tag).append(" \"").append(value).append("\"]\n");
                    }
                    pgn.append("\n");

                    // Add moves
                    pgn.append(chosenGame.moves());

                    String game = pgn.toString();
                    System.out.println("Exported game: " + game);
                    File exportedFile = new File("src/main/java/pgns/exported_games.pgn");
                    try (FileWriter writer = new FileWriter(exportedFile, true)) {  // append = true
                        writer.write(game);
                        writer.write(System.lineSeparator());  // optional: adds newline after each game
                        System.out.println("Game exported.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame, "Error loading games from DB: " + ex.getMessage());
        }
    }
    /**
     * Prepares game descriptions for the selection dialog.
     * @param dbGames List of games from the database
     * @return Array of formatted game descriptions
     */
    private String[] prepareGameOptions(List<ChessGame> dbGames) {
        String[] gameOptions = new String[dbGames.size()];
        for (int i = 0; i < dbGames.size(); i++) {
            Map<String, String> tags = dbGames.get(i).tags();
            String white = tags.getOrDefault("White", "?").replaceAll("\"", "").trim();
            String black = tags.getOrDefault("Black", "?").replaceAll("\"", "").trim();
            String event = tags.getOrDefault("Event", "?").replaceAll("\"", "").trim();
            gameOptions[i] = String.format("Game %d: %s vs %s (%s)", i+1, white, black, event);
        }
        return gameOptions;
    }

    /**
     * Shows the game selection dialog.
     * @param gameOptions Array of game descriptions
     * @return Selected game description or null if cancelled
     */
    private String showGameSelectionDialog(String[] gameOptions) {
        return (String) JOptionPane.showInputDialog(
                parentFrame,
                "Select a game from database:",
                "Choose Game from DB",
                JOptionPane.PLAIN_MESSAGE,
                null,
                gameOptions,
                gameOptions[0]
        );
    }
} 