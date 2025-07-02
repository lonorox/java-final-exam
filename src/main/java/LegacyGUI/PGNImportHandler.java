package LegacyGUI;

import server.Database.DatabaseManager;
import shared.LegacyCore.ChessGame;
import shared.PgnAnalyzers.FileHandler;
import shared.PgnAnalyzers.PGNReader;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Handles the import and processing of PGN files.
 * Provides functionality for importing games and managing the import process.
 */
public class PGNImportHandler {
    private final JFrame parentFrame;
    private List<ChessGame> games;

    /**
     * Creates a new PGNImportHandler.
     * @param parentFrame The parent frame for dialogs
     */
    public PGNImportHandler(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    /**
     * Initiates the PGN file import process.
     * Shows a file chooser and handles the selected file.
     */
    public void importPGNFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(parentFrame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected PGN file: " + selectedFile.getAbsolutePath());
            processPGNFile(selectedFile);
        }
    }

    /**
     * Processes the selected PGN file.
     * @param file The selected PGN file
     */
    private void processPGNFile(File file) {
        try {
            BufferedReader reader = FileHandler.readPgn(file.getAbsolutePath());
            PGNReader pgnReader = new PGNReader();
            pgnReader.extractGames(reader);
            games = pgnReader.getGames();
            
            if (games == null || games.isEmpty()) {
                JOptionPane.showMessageDialog(parentFrame, "No games found in PGN file.");
                return;
            }

            showImportOptions();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parentFrame, "Error reading PGN file: " + ex.getMessage());
        }
    }

    /**
     * Shows options for handling imported games.
     */
    private void showImportOptions() {
        String[] gameOptions = prepareGameOptions();
        String[] mainOptions = {"Import ALL to database", "Select", "Cancel"};
        
        int mainChoice = JOptionPane.showOptionDialog(
                parentFrame,
                "What would you like to do with the imported PGN games?",
                "PGN Import Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                mainOptions,
                mainOptions[0]
        );

        switch (mainChoice) {
            case 0: // Import ALL
                importAllGames();
                break;
            case 1: // Select
                showGameSelection(gameOptions);
                break;
            // case 2 (Cancel) does nothing
        }
    }

    /**
     * Prepares game descriptions for selection dialog.
     * @return Array of formatted game descriptions
     */
    private String[] prepareGameOptions() {
        String[] gameOptions = new String[games.size()];
        for (int i = 0; i < games.size(); i++) {
            Map<String, String> tags = games.get(i).tags();
            String white = tags.getOrDefault("White", "?").replaceAll("\"", "").trim();
            String black = tags.getOrDefault("Black", "?").replaceAll("\"", "").trim();
            String event = tags.getOrDefault("Event", "?").replaceAll("\"", "").trim();
            gameOptions[i] = String.format("Game %d: %s vs %s (%s)", i+1, white, black, event);
        }
        return gameOptions;
    }

    /**
     * Imports all games to the database.
     */
    private void importAllGames() {
        try {
            DatabaseManager dbManager = new DatabaseManager();
            int count = 0;
            for (ChessGame game : games) {
                dbManager.saveGame(game);
                count++;
            }
            JOptionPane.showMessageDialog(parentFrame, count + " games added to database.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame, "Error saving games: " + ex.getMessage());
        }
    }

    /**
     * Shows game selection dialog and handles selected game.
     * @param gameOptions Array of game descriptions
     */
    private void showGameSelection(String[] gameOptions) {
        String selected = (String) JOptionPane.showInputDialog(
                parentFrame,
                "Select a game to load:",
                "Choose Game",
                JOptionPane.PLAIN_MESSAGE,
                null,
                gameOptions,
                gameOptions[0]
        );

        if (selected != null) {
            int selectedIndex = -1;
            for (int i = 0; i < gameOptions.length; i++) {
                if (gameOptions[i].equals(selected)) {
                    selectedIndex = i;
                    break;
                }
            }

            if (selectedIndex != -1) {
                handleSelectedGame(games.get(selectedIndex));
            }
        }
    }

    /**
     * Handles actions for a selected game.
     * @param chosenGame The selected chess game
     */
    private void handleSelectedGame(ChessGame chosenGame) {
        String[] actions = {"Import SELECTED game to database", "Review selected game"};
        int action = JOptionPane.showOptionDialog(
                parentFrame,
                "What would you like to do?",
                "PGN Import Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                actions,
                actions[0]
        );

        if (action == 0) { // Import selected game
            try {
                DatabaseManager dbManager = new DatabaseManager();
                dbManager.saveGame(chosenGame);
                JOptionPane.showMessageDialog(parentFrame, "Selected game added to database.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Error saving game: " + ex.getMessage());
            }
        } else if (action == 1) { // Review game
            GameReviewManager reviewManager = new GameReviewManager(parentFrame);
            reviewManager.reviewGame(chosenGame);
        }
    }
} 