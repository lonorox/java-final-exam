package NewGUI.View;

import NewGUI.Controller.ChessController;
import NewGUI.Model.Clock;
import NewGUI.Model.PieceColor;

import javax.swing.*;
import java.awt.*;

/**
 * The main window frame for the Chess game application.
 * Serves as the primary container that holds all UI components.
 */
public class GameWindow extends JFrame {
    private ChessBoardUI chessBoardUI;
    // Add to GameWindow class
    private Clock whiteClock;
    private Clock blackClock;
    private JLabel whiteClockLabel;
    private JLabel blackClockLabel;
    private Timer clockTimer;
    private JPanel sidePanel;
    private JLabel statusLabel;
    private JButton newGameButton;
    private JButton surrenderButton;
    private ChessController controller;
    private StartMenu startMenu;

    /**
     * Constructs a new GameWindow with the given controller.
     *
     * @param controller The chess game controller
     */
    public GameWindow(ChessController controller) {
        this.controller = controller;

        // Set up the window
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initialize components
        initializeComponents();

        // Display start menu initially
        showStartMenu();
        controller.setView(this); //+ -

        // Set window properties
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    private void initializeClocks(int hours, int minutes, int seconds) {
        // Stop existing timer if running
        if (clockTimer != null && clockTimer.isRunning()) {
            clockTimer.stop();
        }
        
        // Clear existing clock labels from side panel
        if (sidePanel != null) {
            // Remove existing clock components
            for (Component comp : sidePanel.getComponents()) {
                if (comp instanceof JLabel && ((JLabel) comp).getText().contains("Time:")) {
                    sidePanel.remove(comp);
                }
            }
        }
        
        whiteClock = new Clock(hours, minutes, seconds);
        blackClock = new Clock(hours, minutes, seconds);

        whiteClockLabel = new JLabel(whiteClock.getTime());
        blackClockLabel = new JLabel(blackClock.getTime());

        whiteClockLabel.setFont(new Font("Arial", Font.BOLD, 16));
        blackClockLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Add clock labels to the side panel
        sidePanel.add(new JLabel("White Time:"));
        sidePanel.add(whiteClockLabel);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(new JLabel("Black Time:"));
        sidePanel.add(blackClockLabel);
        sidePanel.add(Box.createVerticalStrut(20));

        // Start the clock timer
        startClockTimer();
    }

    /**
     * Initialize all UI components.
     */
    private void initializeComponents() {
        // Create the chess board UI
        chessBoardUI = new ChessBoardUI(controller);

        // Create side panel with game controls
        createSidePanel();

        // Create start menu
        startMenu = new StartMenu(this, controller);
    }
    private void startClockTimer() {
        clockTimer = new Timer(1000, e -> {
            PieceColor currentTurn = controller.getCurrentTurn();

            if (currentTurn == PieceColor.WHITE) {
                whiteClock.decr();
                whiteClockLabel.setText(whiteClock.getTime());

                if (whiteClock.outOfTime()) {
                    clockTimer.stop();
                    showGameOver("Black wins on time!");
                }
            } else {
                blackClock.decr();
                blackClockLabel.setText(blackClock.getTime());

                if (blackClock.outOfTime()) {
                    clockTimer.stop();
                    showGameOver("White wins on time!");
                }
            }
        });
        clockTimer.start();
    }
    /**
     * Creates the side panel with game controls and status information.
     */
    private void createSidePanel() {
        sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Status label
        statusLabel = new JLabel("White's turn");
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Game control buttons
        newGameButton = new JButton("New Game");
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        newGameButton.addActionListener(e -> showStartMenu());

        surrenderButton = new JButton("Surrender");
        surrenderButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        surrenderButton.addActionListener(e -> {
            controller.surrender();
        });

        // Add components to side panel
        sidePanel.add(Box.createVerticalGlue());
        sidePanel.add(statusLabel);
        sidePanel.add(Box.createVerticalStrut(20));
        sidePanel.add(newGameButton);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(surrenderButton);
        sidePanel.add(Box.createVerticalGlue());
    }

    /**
     * Shows the start menu and hides the game board.
     */
    public void showStartMenu() {
        getContentPane().removeAll();
        getContentPane().add(startMenu, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Starts a new game with the specified game mode.
     *
     * @param gameMode The selected game mode (e.g., "Player vs Player")
     */
    public void startGame(String gameMode,int h, int m, int s) {
        getContentPane().removeAll();
        getContentPane().add(chessBoardUI, BorderLayout.CENTER);
        
        // Recreate the side panel to ensure clean state
        createSidePanel();
        getContentPane().add(sidePanel, BorderLayout.EAST);

        // Reset the game state in the controller
        controller.startNewGame(gameMode);
        initializeClocks(h, m, s);

        updateStatus("White's turn");

        revalidate();
        repaint();
    }

    /**
     * Updates the status label with the current game state.
     *
     * @param status The status message to display
     */
    public void updateStatus(String status) {
        statusLabel.setText(status);
    }

    /**
     * Shows a game over dialog with the specified message.
     *
     * @param message The game over message
     */
    public void showGameOver(String message) {
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Refreshes the chess board UI to reflect the current game state.
     */
    public void refreshBoard() {
        if (chessBoardUI != null) {
            chessBoardUI.updateBoard();
        }
    }

    /**
     * Returns the ChessBoardUI component.
     *
     * @return The ChessBoardUI
     */
    public ChessBoardUI getChessBoardUI() {
        return chessBoardUI;
    }
}
