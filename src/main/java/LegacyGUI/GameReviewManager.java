package LegacyGUI;

import server.GM.GameMaster;
import server.GM.GameResult;
import shared.LegacyCore.ChessGame;

import javax.swing.*;
import java.awt.*;

/**
 * Manages the review of chess games.
 * Handles game validation and creates the review interface.
 */
public class GameReviewManager {
    private final JFrame parentFrame;

    /**
     * Creates a new GameReviewManager.
     * @param parentFrame The parent frame for dialogs
     */
    public GameReviewManager(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }

    /**
     * Reviews a chess game by validating it and showing the review interface.
     * @param game The chess game to review
     */
    public void reviewGame(ChessGame game) {
        GameMaster gm = new GameMaster(game.moves());
        GameResult result = gm.analyzeGame();
        
        if (!result.isValid()) {
            JOptionPane.showMessageDialog(
                    parentFrame,
                    "This game is invalid and cannot be reviewed.\nReason: " + result.getLog(),
                    "Invalid Game",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        showReviewInterface(game);
    }

    /**
     * Shows the game review interface.
     * @param game The chess game to review
     */
    private void showReviewInterface(ChessGame game) {
        JFrame replayFrame = new JFrame("Chess Game Review");
        replayFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        replayFrame.setSize(520, 560);
        replayFrame.setLayout(new BorderLayout());
        replayFrame.add(new ChessReplayPanel(game), BorderLayout.CENTER);
        replayFrame.setLocationRelativeTo(null);
        replayFrame.setVisible(true);
    }
} 