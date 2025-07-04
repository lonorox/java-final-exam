package Chess;


import LegacyGUI.PGNImportHandler;
import LegacyGUI.DatabaseGameHandler;
import LegacyGUI.GameReviewManager;
import client.NewGUI.Controller.ChessController;
import client.NewGUI.View.GameWindow;
import client.NewGUI.View.StartMenu;

import javax.swing.*;
import java.awt.*;


/**
 * ChessGUI is the main graphical user interface for the chess application.
 * It provides functionality for importing PGN files, playing games, and reviewing games from a database.
 */
public class ChessGUI {
    private JFrame frame;
    private PGNImportHandler pgnHandler;
    private DatabaseGameHandler dbHandler;
    private GameReviewManager reviewManager;

    /**
     * Launches the main GUI window with all necessary components and event handlers.
     */
    public void launch() {
        frame = new JFrame("Chess Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 150);
        frame.setLayout(new FlowLayout());

        // Initialize handlers
        pgnHandler = new PGNImportHandler(frame);
        dbHandler = new DatabaseGameHandler(frame);
        reviewManager = new GameReviewManager(frame);

        // Create main action buttons
        JButton importButton = new JButton("Import PGN");
        JButton exportButton = new JButton("Export To PGN");
        JButton playButton = new JButton("Play Game");
        JButton dbButton = new JButton("Open Game from DB");
        JButton quitButton = new JButton("Quit");

        // Setup PGN import functionality
        importButton.addActionListener(e -> pgnHandler.importPGNFile());
        // Setup export functionality
        exportButton.addActionListener(e -> dbHandler.exportGameToPGN());
        // Setup play game button
        playButton.addActionListener(e -> {
            // Start the new MVC-based chess game with start menu
            startNewMVCGame();
        });

        // Setup database game access
        dbButton.addActionListener(e -> dbHandler.openGameFromDatabase());

        // Setup quit button
        quitButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to quit?",
                "Confirm Quit",
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        // Add buttons to frame
        frame.add(importButton);
        frame.add(exportButton);
        frame.add(playButton);
        frame.add(dbButton);
        frame.add(quitButton);

        // Center and show frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Starts a new MVC-based chess game with start menu.
     */
    private void startNewMVCGame() {
        SwingUtilities.invokeLater(() -> {
            ChessController controller = new ChessController();
            GameWindow gameWindow = new GameWindow(controller);
            SwingUtilities.invokeLater(new StartMenu(gameWindow, controller));
        });
    }
} 