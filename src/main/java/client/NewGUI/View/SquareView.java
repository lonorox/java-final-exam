package client.NewGUI.View;

import client.NewGUI.Model.Piece;
import client.NewGUI.Model.Position;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

/**
 * Represents a single square on the chess board UI.
 */
public class SquareView extends JPanel {
    private static final Color LIGHT_SQUARE_COLOR = new Color(240, 217, 181);
    private static final Color DARK_SQUARE_COLOR = new Color(181, 136, 99);
    private static final Color SELECTED_SQUARE_COLOR = new Color(130, 151, 105);
    private static final Color LEGAL_MOVE_COLOR = new Color(130, 151, 105, 128);

    private final Position position;
    private final boolean isLightSquare;
    private Piece occupyingPiece;
    private boolean isSelected;
    private boolean isLegalMove;

    /**
     * Constructs a new SquareView at the specified position.
     *
     * @param position The board position this square represents
     * @param isLightSquare Whether this is a light-colored square
     */
    public SquareView(Position position, boolean isLightSquare) {
        this.position = position;
        this.isLightSquare = isLightSquare;
        this.occupyingPiece = null;
        this.isSelected = false;
        this.isLegalMove = false;

        setPreferredSize(new Dimension(75, 75));
        setBackground(isLightSquare ? LIGHT_SQUARE_COLOR : DARK_SQUARE_COLOR);
        setBorder(BorderFactory.createEmptyBorder());
    }

    /**
     * Sets the piece occupying this square.
     *
     * @param piece The piece to place on this square, or null to remove
     */
    public void setOccupyingPiece(Piece piece) {
        this.occupyingPiece = piece;
        repaint();
    }

    /**
     * Gets the piece occupying this square.
     *
     * @return The occupying piece, or null if empty
     */
    public Piece getOccupyingPiece() {
        return occupyingPiece;
    }

    /**
     * Checks if this square is occupied by a piece.
     *
     * @return True if occupied, false otherwise
     */
    public boolean isOccupied() {
        return occupyingPiece != null;
    }

    /**
     * Sets whether this square is selected.
     *
     * @param selected True if selected, false otherwise
     */
    public void setSelected(boolean selected) {
        this.isSelected = selected;
        repaint();
    }

    /**
     * Sets whether this square represents a legal move.
     *
     * @param legalMove True if a legal move, false otherwise
     */
    public void setLegalMove(boolean legalMove) {
        this.isLegalMove = legalMove;
        repaint();
    }

    /**
     * Gets the position this square represents.
     *
     * @return The board position
     */
    public Position getPosition() {
        return position;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the square with the appropriate color
        if (isSelected) {
            setBackground(SELECTED_SQUARE_COLOR);
        } else if (isLegalMove) {
            setBackground(isLightSquare ? LIGHT_SQUARE_COLOR : DARK_SQUARE_COLOR);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(LEGAL_MOVE_COLOR);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.dispose();
        } else {
            setBackground(isLightSquare ? LIGHT_SQUARE_COLOR : DARK_SQUARE_COLOR);
        }

        // Draw the piece if present
        if (occupyingPiece != null) {
            drawPiece(g);
        }

        // Draw coordinates in small text in the corner (optional)
        g.setColor(isLightSquare ? DARK_SQUARE_COLOR : LIGHT_SQUARE_COLOR);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString(position.toString(), 2, 12);
    }

    /**
     * Draws the chess piece on this square.
     *
     * @param g The graphics context
     */
    private void drawPiece(Graphics g) {
        if (occupyingPiece == null) return;

        try {
            // Get just the filename from the piece
            String imageFileName = occupyingPiece.getImageFile();

            // Try multiple methods to load the image
            Image pieceImage = loadPieceImageMultipleWays(imageFileName);

            if (pieceImage != null) {
                // Center the piece image on the square
                int x = (getWidth() - pieceImage.getWidth(null)) / 2;
                int y = (getHeight() - pieceImage.getHeight(null)) / 2;
                g.drawImage(pieceImage, x, y, null);
            } else {
                // Fallback to drawing a simple representation
                drawFallbackPiece(g);
            }
        } catch (Exception e) {
            System.err.println("Error drawing piece: " + e.getMessage());
            // Fallback to drawing a simple representation
            drawFallbackPiece(g);
        }
    }

    /**
     * Attempts to load the piece image using multiple methods.
     *
     * @param imageFileName The image file name
     * @return The loaded image, or null if all loading methods failed
     */
    private Image loadPieceImageMultipleWays(String imageFileName) {
        Image image = null;

        // Method 1: Try to load from class resources
        try {
            URL resourceUrl = getClass().getResource("/images/" + imageFileName);
            if (resourceUrl != null) {
                return ImageIO.read(resourceUrl);
            }
        } catch (Exception e) {
            System.err.println("Failed to load from class resources: " + e.getMessage());
        }

        // Method 2: Try to load from classpath root resources
        try {
            URL resourceUrl = getClass().getClassLoader().getResource("images/" + imageFileName);
            if (resourceUrl != null) {
                return ImageIO.read(resourceUrl);
            }
        } catch (Exception e) {
            System.err.println("Failed to load from classpath root: " + e.getMessage());
        }

        // Method 3: Try to load from file system relative to project
        try {
            File file = new File("resources/images/" + imageFileName);
            if (file.exists()) {
                return ImageIO.read(file);
            }
        } catch (Exception e) {
            System.err.println("Failed to load from resources/images directory: " + e.getMessage());
        }

        // Method 4: Try src/main/resources path
        try {
            File file = new File("src/main/resources/images/" + imageFileName);
            if (file.exists()) {
                return ImageIO.read(file);
            }
        } catch (Exception e) {
            System.err.println("Failed to load from src/main/resources/images: " + e.getMessage());
        }

        // Method 5: Try direct project structure
        try {
            File file = new File("src/resources/images/" + imageFileName);
            if (file.exists()) {
                return ImageIO.read(file);
            }
        } catch (Exception e) {
            System.err.println("Failed to load from src/resources/images: " + e.getMessage());
        }

        // Method 6: Try direct "images" directory
        try {
            File file = new File("images/" + imageFileName);
            if (file.exists()) {
                return ImageIO.read(file);
            }
        } catch (Exception e) {
            System.err.println("Failed to load from images directory: " + e.getMessage());
        }

        return null;
    }

    /**
     * Draws a fallback representation of the chess piece when image loading fails.
     *
     * @param g The graphics context
     */
    private void drawFallbackPiece(Graphics g) {
        g.setColor(occupyingPiece.getColor().toString().equals("WHITE") ? Color.WHITE : Color.BLACK);
        g.fillOval(15, 15, getWidth() - 30, getHeight() - 30);
        g.setColor(occupyingPiece.getColor().toString().equals("WHITE") ? Color.BLACK : Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String pieceType = occupyingPiece.getType();
        g.drawString(pieceType.substring(0, 1), getWidth() / 2 - 8, getHeight() / 2 + 8);
    }
}