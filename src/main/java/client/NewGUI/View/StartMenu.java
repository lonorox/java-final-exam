package client.NewGUI.View;

import client.NewGUI.Controller.ChessController;
import client.NewGUI.Model.PlayerInfo;
import server.Database.PlayerDatabaseManager;
import client.Network.ChessClient;
import client.Network.MessageHandler;
import server.Player;
import shared.Message;
import shared.Protocol;
import client.NewGUI.Model.PieceColor;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Start menu UI that allows players to configure and start a new chess game.
 */
public class StartMenu extends JPanel implements Runnable {
    private final GameWindow parentWindow;
    private final ChessController controller;
    private JComboBox<String> gameModeSelector;
    private JButton startButton;
    private JButton loginButton;
    private JButton registerButton;
    private JButton hostButton;
    private JButton joinButton;
    private JLabel playerLabel;
    private JComboBox<String> hoursComboBox;
    private JComboBox<String> minutesComboBox;
    private JComboBox<String> secondsComboBox;
    private String player;
    private PlayerDatabaseManager playerDB;
    private ChessClient chessClient;
    private ChessController gameController; // Store reference to controller for move handling
    /**
     * Constructs a new StartMenu within the parent window.
     *
     * @param parentWindow The parent GameWindow
     * @param controller The chess game controller
     */
    public StartMenu(GameWindow parentWindow, ChessController controller) {
        this.parentWindow = parentWindow;
        this.controller = controller;
        this.player = "Guest";
        this.playerDB = new PlayerDatabaseManager();
        // Set up the panel
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Create title and content panels
        JPanel titlePanel = createTitlePanel();
        JPanel contentPanel = createContentPanel();
        JPanel buttonPanel = createButtonPanel();

        // Add panels to layout
        add(titlePanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the title panel with game title and logo.
     *
     * @return The title panel
     */
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Create game title
        JLabel titleLabel = new JLabel("Chess Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        playerLabel = new JLabel("Player: "+ player);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(playerLabel);
        // Add components to panel
        panel.add(Box.createVerticalStrut(20));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        return panel;
    }

    /**
     * Creates the content panel with game settings.
     *
     * @return The content panel
     */
    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Game mode selector (existing code)
        JLabel gameModeLabel = new JLabel("Select Game Mode:");
        gameModeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameModeLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        String[] gameModes = {"Player vs Player", "Player vs Computer"};
        gameModeSelector = new JComboBox<>(gameModes);
        gameModeSelector.setMaximumSize(new Dimension(300, 30));
        gameModeSelector.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add clock settings
        JLabel clockLabel = new JLabel("Set Game Clock:");
        clockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        clockLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        // Create time selectors
        String[] hours = {"0", "1", "2", "3"};
        hoursComboBox = new JComboBox<>(hours);

        String[] minutesSeconds = new String[60];
        for (int i = 0; i < 60; i++) {
            minutesSeconds[i] = i < 10 ? "0" + i : String.valueOf(i);
        }

        minutesComboBox = new JComboBox<>(minutesSeconds);
        secondsComboBox = new JComboBox<>(minutesSeconds);

        // Set default time to 10 minutes
        minutesComboBox.setSelectedItem("10");

        // Time selector panel

        JPanel timePanel = new JPanel();
        timePanel.add(new JLabel("Hours:"));
        timePanel.add(hoursComboBox);
        timePanel.add(new JLabel("Minutes:"));
        timePanel.add(minutesComboBox);
        timePanel.add(new JLabel("Seconds:"));
        timePanel.add(secondsComboBox);

        // Add components to panel
        panel.add(Box.createVerticalGlue());
        panel.add(gameModeLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(gameModeSelector);
        panel.add(Box.createVerticalStrut(20));
        panel.add(clockLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(timePanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    /**
     * Creates the button panel with start game button.
     *
     * @return The button panel
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Create start button
        startButton = new JButton("Start Game");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setMaximumSize(new Dimension(200, 50));
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.setVisible(false);

        JPanel buttonGroupPanel = new JPanel();
        buttonGroupPanel.setLayout(new BoxLayout(buttonGroupPanel, BoxLayout.Y_AXIS));
        Dimension buttonSize = new Dimension(150, 40);
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        hostButton = new JButton("Host");
        joinButton = new JButton("Join");
        JPanel row1 = new JPanel(new GridLayout(1, 2, 20, 0)); // 1 row, 2 columns, 20px horizontal gap
        row1.setMaximumSize(new Dimension(340, 50)); // width = 150 + 150 + gap
        row1.add(loginButton);
        row1.add(registerButton);

// Row 2: Host + Join
        JPanel row2 = new JPanel(new GridLayout(1, 2, 20, 0));
        row2.setMaximumSize(new Dimension(340, 50));
        row2.add(hostButton);
        row2.add(joinButton);


        buttonGroupPanel.add(Box.createVerticalStrut(10));
        buttonGroupPanel.add(row1);
        buttonGroupPanel.add(Box.createVerticalStrut(10));
        buttonGroupPanel.add(row2);
        buttonGroupPanel.add(Box.createVerticalStrut(10));
        // Create cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cancelButton.setMaximumSize(new Dimension(200, 50));
        cancelButton.setFont(new Font("Arial", Font.BOLD, 18));

        // Add action listener to start button
        startButton.addActionListener(e -> {
            String selectedMode = (String) gameModeSelector.getSelectedItem();
            int hours = Integer.parseInt((String) hoursComboBox.getSelectedItem());
            int minutes = Integer.parseInt((String) minutesComboBox.getSelectedItem());
            int seconds = Integer.parseInt((String) secondsComboBox.getSelectedItem());

            // Pass clock settings to start game
            parentWindow.startGame(selectedMode, hours, minutes, seconds);
        });

        // Add action listener to cancel button
        cancelButton.addActionListener(e -> {
            // Close the game window and return to main GUI
            parentWindow.dispose();
        });
        panel.add(Box.createVerticalStrut(20));
        gameModeSelector.addActionListener(e -> {
            String selected = (String) gameModeSelector.getSelectedItem();
            System.out.println(player);

            if (selected.equals("Player vs Player")) {
                joinButton.setVisible(true);
                hostButton.setVisible(true);
                startButton.setVisible(false);
            } else{
                startButton.setVisible(true);
                joinButton.setVisible(false);
                hostButton.setVisible(false);
            }
            if(player.equals("Guest")){
                loginButton.setVisible(true);
                registerButton.setVisible(true);
            }else{
                loginButton.setVisible(false);
                registerButton.setVisible(false);
            }
        });

        // Add action listener for login button
        loginButton.addActionListener(e -> {
            JPanel inputPanel = new JPanel(new GridLayout(2, 2));
            JLabel userLabel = new JLabel("Username:");
            JTextField userField = new JTextField();
            JLabel passLabel = new JLabel("Password:");
            JPasswordField passField = new JPasswordField();
            inputPanel.add(userLabel);
            inputPanel.add(userField);
            inputPanel.add(passLabel);
            inputPanel.add(passField);
            int result = JOptionPane.showConfirmDialog(this, inputPanel, "Login", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                try {
                    String user = playerDB.getUser(username, password);
                    if(user != null) {
                        player = user;
                        playerLabel.setText("Player: " + player);
                        loginButton.setVisible(false);
                        registerButton.setVisible(false);
                        System.out.println(player);
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        // Add action listener for register button
        registerButton.addActionListener(e -> {
            JPanel inputPanel = new JPanel(new GridLayout(2, 2));
            JLabel userLabel = new JLabel("Username:");
            JTextField userField = new JTextField();
            JLabel passLabel = new JLabel("Password:");
            JPasswordField passField = new JPasswordField();
            inputPanel.add(userLabel);
            inputPanel.add(userField);
            inputPanel.add(passLabel);
            inputPanel.add(passField);
            int result = JOptionPane.showConfirmDialog(this, inputPanel, "Register", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {

                String username = userField.getText();
                String password = new String(passField.getPassword());
                try {
                    playerDB.register(username,password);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        // Add action listener for host button
        hostButton.addActionListener(e -> {
            // First try to connect to existing server
            try {
                connectToServer("localhost");
            } catch (Exception ex) {
                // If connection fails, try to start the server
                new Thread(() -> {
                    try {
                        Class<?> serverMainClass = Class.forName("server.ServerMain");
                        serverMainClass.getMethod("main", String[].class).invoke(null, (Object) new String[]{});
                    } catch (Exception serverEx) {
                        serverEx.printStackTrace();
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Failed to start server: " + serverEx.getMessage()));
                    }
                }).start();
                // Wait a moment for server to start, then try to connect
                new Thread(() -> {
                    try {
                        Thread.sleep(2000); // Wait 2 seconds for server to start
                        SwingUtilities.invokeLater(() -> {
                            try {
                                connectToServer("localhost");
                            } catch (Exception ex2) {
                                ex2.printStackTrace();
                                JOptionPane.showMessageDialog(StartMenu.this, "Failed to connect to server: " + ex2.getMessage());
                            }
                        });
                    } catch (Exception ex2) {
                        ex2.printStackTrace();
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Failed to connect to server: " + ex2.getMessage()));
                    }
                }).start();
            }
        });
        // Add action listener for join button
        joinButton.addActionListener(e -> {
            String hostIP = JOptionPane.showInputDialog(this, "Enter Host IP Address:", "localhost");
            if (hostIP != null && !hostIP.isEmpty()) {
                try {
                    connectToServer(hostIP);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to connect to server: " + ex.getMessage());
                }
            }
        });
        panel.add(startButton);
        panel.add(buttonGroupPanel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(cancelButton);
        panel.add(Box.createVerticalStrut(20));

        return panel;
    }

    // Helper method to connect to the server and send login message
    private void connectToServer(String serverAddress) throws Exception {
        chessClient = new ChessClient();
        chessClient.setMessageHandler(new MessageHandler() {
            @Override
            public void handle(Message message) {
                // Handle initial game setup messages
                if (Protocol.GAME_STATE.equals(message.getType())) {
                    if (message.getContent() instanceof List) {
                        // Game is starting - we have both players
                        List<?> contentList = (List<?>) message.getContent();
                        String content = (String) contentList.get(0);
                        if (content.contains("You are playing as WHITE") || content.contains("You are playing as BLACK")) {
                            PieceColor assignedColor = content.contains("WHITE") ? PieceColor.WHITE : PieceColor.BLACK;
                            String opponentName = (String) contentList.get(1);
                            // Get clock settings
                            int hours = Integer.parseInt((String) hoursComboBox.getSelectedItem());
                            int minutes = Integer.parseInt((String) minutesComboBox.getSelectedItem());
                            int seconds = Integer.parseInt((String) secondsComboBox.getSelectedItem());
                            // Open the game window with network info
                            SwingUtilities.invokeLater(() -> {
                                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(StartMenu.this);
                                if (topFrame != null) topFrame.dispose();
                                ChessController newController = new ChessController();
                                gameController = newController; // Store reference
                                String localPlayerName = (player != null && !player.isEmpty()) ? player : "Guest";
                                PlayerInfo user = new PlayerInfo(localPlayerName,assignedColor.name());
                                PlayerInfo opponent = new PlayerInfo(opponentName,assignedColor.opposite().name());
                                GameWindow gameWindow = new GameWindow(newController, chessClient, assignedColor, user, opponent);
                                gameWindow.startGame("Player vs Player", hours, minutes, seconds);
                            // Set up the message handler for move handling in GameWindow
                            gameWindow.setupMessageHandler();
                            });
                        }
                    } else if (message.getContent() instanceof String) {
                        // Waiting for opponent
                        String content = (String) message.getContent();
                        if (content.contains("Waiting for opponent")) {
                            JOptionPane.showMessageDialog(StartMenu.this, content);
                        }
                    }
                } else {
                    // Other messages (for debug)
                    System.out.println("StartMenu received: " + message.getType() + " - " + message.getContent());
                }
            }
        });
        chessClient.connect(serverAddress, Protocol.PORT);
        // Send login message with username (or Guest if not logged in)
        String usernameToSend = (player != null && !player.isEmpty()) ? player : "Guest";
        chessClient.sendMessage(new Message(Protocol.LOGIN, usernameToSend));
        JOptionPane.showMessageDialog(this, "Connected to server as " + usernameToSend + ". Waiting for game to start...");
        }

    @Override
    public void run() {

    }
}
