package shared.LegacyCore;

import server.GM.GameMaster;
import shared.PgnAnalyzers.MoveInfo;
import Pieces.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static server.GM.moveValidators.isCheckMate;
import static server.GM.moveValidators.isChecked;

/**
 * ChessGamePanel provides an interactive interface for playing chess games.
 * It handles piece movement, turn management, and game state.
 */
public class ChessGamePanel extends JPanel {
    private Board board;                    // The chess board state
    private Map<String, Image> pieceImages; // Cache for piece images
    private JLabel statusLabel;             // Label showing current game status
    private boolean isWhiteTurn = true;     // Tracks whose turn it is
    private int selectedRow = -1;           // Currently selected piece row
    private int selectedCol = -1;           // Currently selected piece column
    private GameMaster gameMaster;          // Handles move validation
    private boolean gameOver = false;       // Tracks if the game is over

    public ChessGamePanel() {
        setLayout(new BorderLayout());
        this.board = new Board();
        this.pieceImages = new HashMap<>();
        this.gameMaster = new GameMaster("");
        this.gameMaster.setBoard(board);
        loadPieceImages();

        // Create and setup the chessboard panel
        JPanel boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        boardPanel.setPreferredSize(new Dimension(480, 480));
        boardPanel.addMouseListener(new ChessMouseListener());

        // Center the board panel
        JPanel boardPanelWrapper = new JPanel(new GridBagLayout());
        boardPanelWrapper.add(boardPanel);
        add(boardPanelWrapper, BorderLayout.CENTER);

        // Setup control panel with status and buttons
        statusLabel = new JLabel("White's turn", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JButton newGameButton = new JButton("New Game");
        JButton closeButton = new JButton("Close");

        JPanel controlPanel = new JPanel();
        controlPanel.add(statusLabel);
        controlPanel.add(newGameButton);
        controlPanel.add(closeButton);
        add(controlPanel, BorderLayout.SOUTH);

        // Setup button actions
        newGameButton.addActionListener(e -> resetGame());
        closeButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(ChessGamePanel.this);
            if (window != null) {
                window.dispose();
            }
        });
    }

    /**
     * Resets the game to its initial state.
     */
    private void resetGame() {
        board = new Board();
        gameMaster.setBoard(board);
        isWhiteTurn = true;
        selectedRow = -1;
        selectedCol = -1;
        gameOver = false;
        statusLabel.setText("White's turn");
        repaint();
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
     */
    private void drawBoard(Graphics g) {
        int tileSize = 60;
        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col < 8; col++) {
                int x = col * tileSize;
                int y = (7 - row) * tileSize;
                
                // Draw board square
                boolean isLight = (row + col) % 2 == 0;
                g.setColor(isLight ? new Color(240, 217, 181) : new Color(181, 136, 99));
                g.fillRect(x, y, tileSize, tileSize);

                // Highlight selected square
                if (row == selectedRow && col == selectedCol) {
                    g.setColor(new Color(255, 255, 0, 100));
                    g.fillRect(x, y, tileSize, tileSize);
                }

                // Draw piece
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

    /**
     * Mouse listener for handling piece selection and movement.
     */
    private class ChessMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (gameOver) return;

            int tileSize = 60;
            int col = e.getX() / tileSize;
            int row = 7 - (e.getY() / tileSize);

            if (row >= 0 && row < 8 && col >= 0 && col < 8) {
                Piece clickedPiece = board.getPiece(row, col);
                
                // If no piece is selected, select a piece of the current player's color
                if (selectedRow == -1) {
                    if (clickedPiece != null && clickedPiece.getColor().equals(isWhiteTurn ? "white" : "black")) {
                        selectedRow = row;
                        selectedCol = col;
                        System.out.println("Selected piece: " + clickedPiece.getColor() + " " + clickedPiece.getType() + " at (" + row + ", " + col + ")");
                    }
                } else {
                    // Try to move the selected piece
                    Piece selectedPiece = board.getPiece(selectedRow, selectedCol);
                    if (selectedPiece != null) {
                        // Create move info for validation
                        MoveInfo move = new MoveInfo();
                        move.piece = selectedPiece.getType().substring(0, 1);
                        move.destination = String.format("%c%d", (char)('a' + col), row + 1);
                        move.location = String.format("%c%d", (char)('a' + selectedCol), selectedRow + 1);

                        // Check if it's a capture
                        if (clickedPiece != null) {
                            move.capture = true;
                        }

//                        move.castlingKing = false;
//                        move.castlingQueen = false;
//                        move.enPassant = false;
//                        move.promotion = "";

                        // Try to validate and execute the move
                        if (gameMaster.analyzeMove(move, isWhiteTurn)) {
                            // Move was successful, switch turns
                            isWhiteTurn = !isWhiteTurn;
                            statusLabel.setText((isWhiteTurn ? "White" : "Black") + "'s turn");
                            
                            // Check for check/checkmate
                            if (isChecked(board, row, col, !isWhiteTurn, true)) {
                                if (isCheckMate(board, row, col, move, !isWhiteTurn)) {
                                    gameOver = true;
                                    JOptionPane.showMessageDialog(ChessGamePanel.this, 
                                        (isWhiteTurn ? "Black" : "White") + " wins by checkmate!", 
                                        "Game Over", 
                                        JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    statusLabel.setText(statusLabel.getText() + " (Check!)");
                                }
                            }
                        } else {
                            // Invalid move, show error
                            JOptionPane.showMessageDialog(ChessGamePanel.this, 
                                "Invalid move: " + gameMaster.getMoveError(), 
                                "Invalid Move", 
                                JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    
                    // Reset selection
                    selectedRow = -1;
                    selectedCol = -1;
                }
                
                repaint();
            }
        }
    }
} 