package Chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import LegacyGUI.PGNImportHandler;
import LegacyGUI.DatabaseGameHandler;
import LegacyGUI.GameReviewManager;
import LegacyGUI.DatabaseGameHandler;
import LegacyGUI.GameReviewManager;
import LegacyGUI.PGNImportHandler;


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
        JButton playButton = new JButton("Play Game");
        JButton dbButton = new JButton("Open Game from DB");
        JButton quitButton = new JButton("Quit");

        // Setup PGN import functionality
        importButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pgnHandler.importPGNFile();
            }
        });

        // Setup play game button
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Start the new MVC-based chess game with start menu
                startNewMVCGame();
            }
        });

        // Setup database game access
        dbButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dbHandler.openGameFromDatabase();
            }
        });

        // Setup quit button
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to quit?",
                    "Confirm Quit",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        // Add buttons to frame
        frame.add(importButton);
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
            NewGUI.Controller.ChessController controller = new NewGUI.Controller.ChessController();
            NewGUI.View.GameWindow gameWindow = new NewGUI.View.GameWindow(controller);
            SwingUtilities.invokeLater(new NewGUI.View.StartMenu(gameWindow, controller));
        });
    }
} 