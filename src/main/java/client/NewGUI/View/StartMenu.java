package client.NewGUI.View;

import client.NewGUI.Controller.ChessController;

import javax.swing.*;
import java.awt.*;

/**
 * Start menu UI that allows players to configure and start a new chess game.
 */
public class StartMenu extends JPanel implements Runnable {
    private final GameWindow parentWindow;
    private final ChessController controller;
    private JComboBox<String> gameModeSelector;
    private JButton startButton;
    private JComboBox<String> hoursComboBox;
    private JComboBox<String> minutesComboBox;
    private JComboBox<String> secondsComboBox;


    /**
     * Constructs a new StartMenu within the parent window.
     *
     * @param parentWindow The parent GameWindow
     * @param controller The chess game controller
     */
    public StartMenu(GameWindow parentWindow, ChessController controller) {
        this.parentWindow = parentWindow;
        this.controller = controller;

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

        // Add components to panel
        panel.add(Box.createVerticalStrut(20));
        panel.add(startButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(cancelButton);
        panel.add(Box.createVerticalStrut(20));

        return panel;
    }

    @Override
    public void run() {

    }
}
