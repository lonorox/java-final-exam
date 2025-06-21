package Chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

        // Setup play game button (to be implemented)
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] options = {"Player vs Player", "Player vs Computer", "Cancel"};
                int choice = JOptionPane.showOptionDialog(
                    frame,
                    "Choose game mode:",
                    "Game Mode Selection",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
                );

                if (choice == 0) { // Player vs Player
                    startPlayerVsPlayerGame();
                } else if (choice == 1) { // Player vs Computer
                    JOptionPane.showMessageDialog(frame, "Player vs Computer mode coming soon!");
                }
                // Cancel (choice == 2) does nothing
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
     * Starts a new player vs player chess game in a new window.
     */
    private void startPlayerVsPlayerGame() {
        JFrame gameFrame = new JFrame("Chess Game - Player vs Player");
        gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Create and add the game panel
        ChessGamePanel gamePanel = new ChessGamePanel();
        gameFrame.add(gamePanel);
        
        // Set up the window
        gameFrame.pack();
        gameFrame.setLocationRelativeTo(frame);
        gameFrame.setVisible(true);
    }
} 