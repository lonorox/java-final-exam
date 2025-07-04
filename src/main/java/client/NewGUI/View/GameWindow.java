package client.NewGUI.View;

import client.NewGUI.Controller.ChessController;
import client.NewGUI.Model.Clock;
import client.NewGUI.Model.PieceColor;
import client.Network.ChessClient;
import client.Network.MessageHandler;
import client.NewGUI.Model.PlayerInfo;
import server.Player;
import shared.Message;
import shared.Protocol;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

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
    private JLabel gameLabel;
    private JButton newGameButton;
    private JButton surrenderButton;
    private ChessController controller;
    private StartMenu startMenu;
    private ChessClient chessClient; // For networked play
    private PieceColor playerColor;  // For networked play
    private PlayerInfo player1;
    private PlayerInfo player2;
    /**
     * Constructs a new GameWindow with the given controller, ChessClient, and player color (for networked play).
     *
     * @param controller The chess game controller
     * @param chessClient The network client (null for local play)
     * @param playerColor The player's color (null for local play)
     */
    public GameWindow(ChessController controller, ChessClient chessClient, PieceColor playerColor, PlayerInfo player1, PlayerInfo player2) {
        this.controller = controller;
        this.chessClient = chessClient;
        this.playerColor = playerColor;
        this.player1 = player1;
        this.player2 = player2;
        // Set up the window
        setTitle("Chess Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add window listener for disconnect handling
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (chessClient != null && chessClient.isConnected()) {
                    // Send disconnect message to server
                    chessClient.sendMessage(new Message(Protocol.LOGOUT, "Player disconnected"));
                    chessClient.disconnect();
                }
            }
        });

        // Initialize components
        initializeComponents();

        // Display start menu initially
        showStartMenu();
        controller.setView(this);

        // Set window properties
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Overloaded constructor for backward compatibility (local play)
    public GameWindow(ChessController controller) {
        this(controller, null, null, null, null);
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
        // Create the chess board UI, passing playerColor and chessClient if needed
        chessBoardUI = new ChessBoardUI(controller, playerColor);

        // Create side panel with game controls
        createSidePanel();

        // Create start menu
        startMenu = new StartMenu(this, controller);
    }
    private void startClockTimer() {
        // Don't start clock timer in networked mode to avoid desynchronization
        if (chessClient != null) {
            System.out.println("Clock timer disabled for networked play");
            return;
        }
        
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
            if (chessClient != null && chessClient.isConnected()) {
                // Send surrender message to server
                chessClient.sendMessage(new Message(Protocol.LOGOUT, "Player surrendered"));
                chessClient.disconnect();
            }
            controller.surrender();
        });
//        if(playerColor != null || playerColor.getValue()) {
        if(player1 != null || player2 != null){
            String name1 = player1.name();
            String name2 = player2.name();
            if (Objects.equals(player1.name(), player2.name())) {
                name1 = name1 + "1";
                name2 = name2 + "2";
            }
            gameLabel = new JLabel(name1 + " Versus " + name2);
            gameLabel.setFont(new Font("Arial", Font.ITALIC, 16));
            JLabel p1 = new JLabel(player1.color() + ": "  + name1 + "- YOU");
            JLabel p2 = new JLabel(player2.color() + ": "  + name2);
            p1.setAlignmentX(Component.CENTER_ALIGNMENT);
            p2.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidePanel.add(p1);
            sidePanel.add(p2);
            gameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidePanel.add(gameLabel);
        }
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
        controller.startNewGame(gameMode, chessClient, playerColor);
        
        // Set up message handler for networked play
        if (chessClient != null) {
            // Message handler will be set up by StartMenu after game window is created
            System.out.println("GameWindow: Message handler will be set up by StartMenu");
        }
        
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
    
    /**
     * Gets the white player name.
     * @return The white player name
     */
    public String getWhitePlayerName() {
        return player1 != null ? player1.name() : "Unknown";
    }
    
    /**
     * Gets the black player name.
     * @return The black player name
     */
    public String getBlackPlayerName() {
        return player2 != null ? player2.name() : "Unknown";
    }

    // Set up message handler for receiving moves from server
    public void setupMessageHandler() {
        System.out.println("SETTING UP MESSAGE HANDLER IN GAMEWINDOW");
        chessClient.setMessageHandler(new MessageHandler() {
            @Override
            public void handle(Message message) {
                System.out.println("GAMEWINDOW MESSAGE HANDLER CALLED: " + message.getType() + " - " + message.getContent());
                if (Protocol.MOVE.equals(message.getType()) && message.getContent() instanceof String) {
                    // Handle move messages from server
                    String moveString = (String) message.getContent();
                    System.out.println("GAMEWINDOW RECEIVED MOVE: " + moveString);
                    controller.applyMoveFromServer(moveString);
                } else if (Protocol.GAME_OVER.equals(message.getType())) {
                    // Handle game over messages
                    String result = (String) message.getContent();
                    SwingUtilities.invokeLater(() -> showGameOver(result));
                } else if (Protocol.ERROR.equals(message.getType()) && message.getContent() instanceof String) {
                    // Handle error messages (like opponent disconnect)
                    String errorMessage = (String) message.getContent();
                    if (errorMessage.contains("disconnect") || errorMessage.contains("Disconnected")) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(GameWindow.this, 
                                "Opponent has disconnected from the game.", 
                                "Opponent Disconnected", 
                                JOptionPane.INFORMATION_MESSAGE);
                            dispose(); // Close the game window
                        });
                    }
                } else {
                    // Other messages (for debug)
                    System.out.println("GameWindow received: " + message.getType() + " - " + message.getContent());
                }
            }
        });
        System.out.println("MESSAGE HANDLER SET UP COMPLETE");
    }
}
