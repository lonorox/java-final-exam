package LegacyGUI;

import server.GM.GameMaster;
import shared.LegacyCore.Board;
import shared.LegacyCore.ChessGame;
import shared.PgnAnalyzers.MoveInfo;
import Pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * ChessReplayPanel provides a graphical interface for reviewing chess games step by step.
 * It displays the chessboard, handles move progression, and shows game information.
 */
public class ChessReplayPanel extends JPanel {
    private Board board;                    // The chess board state
    private String[] moves;                 // Array of moves in the game
    private int moveIndex = 0;              // Current move index
    private JButton nextButton;             // Button to progress to next move
    private Map<String, Image> pieceImages; // Cache for piece images
    private JLabel statusLabel;             // Label showing current game status
    private JLabel playerLabel;             // Label showing player names

    /**
     * Creates a new ChessReplayPanel for the specified game.
     * @param game The chess game to be replayed
     */
    public ChessReplayPanel(ChessGame game) {
        setLayout(new BorderLayout());
        this.board = new Board();
        this.moves = game.moves().replaceAll("\\d+\\.\\s*", "").trim().split("\\s+");
        this.pieceImages = new HashMap<>();
        loadPieceImages();

        // Setup player names display
        String white = game.tags().getOrDefault("White", "White").replaceAll("\"", "").trim();
        String black = game.tags().getOrDefault("Black", "Black").replaceAll("\"", "").trim();
        playerLabel = new JLabel("White: " + white + "    Black: " + black, SwingConstants.CENTER);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(playerLabel, BorderLayout.NORTH);

        // Create and setup the chessboard panel
        JPanel boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        boardPanel.setPreferredSize(new Dimension(480, 480));

        // Center the board panel
        JPanel boardPanelWrapper = new JPanel(new GridBagLayout());
        boardPanelWrapper.add(boardPanel);
        add(boardPanelWrapper, BorderLayout.CENTER);

        // Setup control panel with buttons and status
        nextButton = new JButton("Next");
        JButton closeButton = new JButton("Close");
        statusLabel = new JLabel(getStatusText());
        JPanel controlPanel = new JPanel();
        controlPanel.add(nextButton);
        controlPanel.add(closeButton);
        controlPanel.add(statusLabel);
        add(controlPanel, BorderLayout.SOUTH);

        // Disable nextButton if already at the end
        if (moveIndex >= moves.length) {
            nextButton.setEnabled(false);
        }

        // Setup next button action
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (moveIndex < moves.length) {
                    boolean isWhite = moveIndex % 2 == 0;
                    MoveInfo move = new MoveInfo();
                    move.decipherMove(moves[moveIndex]);
                    GameMaster gm = new GameMaster("");
                    gm.setBoard(board);
                    gm.analyzeMove(move, isWhite);
                    moveIndex++;
                    statusLabel.setText(getStatusText());
                    boardPanel.repaint();
                    if (moveIndex >= moves.length) {
                        nextButton.setEnabled(false);
                        // Show winner/result
                        String result = game.tags().getOrDefault("Result", "").replaceAll("\"", "").trim();
                        String winnerMsg = "";
                        if (result.equals("1-0")) winnerMsg = "White wins!";
                        else if (result.equals("0-1")) winnerMsg = "Black wins!";
                        else if (result.equals("1/2-1/2")) winnerMsg = "Draw!";
                        else winnerMsg = "Result: " + result;
                        JOptionPane.showMessageDialog(ChessReplayPanel.this, winnerMsg, "Game Over", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    nextButton.setEnabled(false);
                }
            }
        });

        // Setup close button action
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Window window = SwingUtilities.getWindowAncestor(ChessReplayPanel.this);
                if (window != null) {
                    window.dispose();
                }
            }
        });
    }

    /**
     * Generates the status text showing current move and player.
     * @return Formatted status string
     */
    private String getStatusText() {
        String color = (moveIndex % 2 == 0) ? "White" : "Black";
        return "Step: " + (moveIndex) + " / " + (moves.length-1) + "    Playing: " + color;
    }

    /**
     * Loads chess piece images from the resources directory.
     */
    private void loadPieceImages() {
        String basePath = "src/resources/images/";
        String[] keys = {"wking", "wqueen", "wrook", "wbishop", "wknight", "wpawn", "bking", "bqueen", "brook", "bbishop", "bknight", "bpawn"};
        for (String key : keys) {
            try {
                pieceImages.put(key, ImageIO.read(new File(basePath + key + ".png")));
            } catch (IOException e) {
                System.err.println("Could not load image for: " + key);
            }
        }
    }

    /**
     * Draws the chessboard and pieces on the graphics context.
     * @param g The graphics context to draw on
     */
    private void drawBoard(Graphics g) {
        int tileSize = 60;
        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col < 8; col++) {
                int x = col * tileSize;
                int y = (7 - row) * tileSize;
                boolean isLight = (row + col) % 2 == 0;
                g.setColor(isLight ? new Color(240, 217, 181) : new Color(181, 136, 99));
                g.fillRect(x, y, tileSize, tileSize);
                Piece piece = board.getPiece(row, col);
                if (piece != null) {
                    String color = piece.getColor().equals("white") ? "w" : "b";
                    String type = piece.getType().toLowerCase();
                    if (type.equals("night")) type = "knight";
                    String key = color + type;
                    Image img = pieceImages.get(key);
                    if (img != null) {
                        g.drawImage(img, x + 5, y + 5, tileSize - 10, tileSize - 10, null);
                    }
                }
            }
        }
    }
} 