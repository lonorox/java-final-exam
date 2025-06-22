package LegacyGUI;

import Database.DatabaseManager;
import LegacyCore.ChessGame;

import javax.swing.*;
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
                handleSelectedGame(dbGames, gameOptions, selected);
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

    /**
     * Handles the selected game from the database.
     * @param dbGames List of games from the database
     * @param gameOptions Array of game descriptions
     * @param selected Selected game description
     */
    private void handleSelectedGame(List<ChessGame> dbGames, String[] gameOptions, String selected) {
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
} 