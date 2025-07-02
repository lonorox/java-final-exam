package client.NewGUI.Controller;

import client.NewGUI.View.GameWindow;
import client.NewGUI.Model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Main controller for the chess game that connects the model and view.
 * Handles game logic and user interactions.
 */
public class ChessController implements ChessControllerInterface {
    private GameState gameState;
    private GameWindow view;
    private String gameMode;
    private int moveCount;
    private int whiteScore;
    private int blackScore;
    private List<String> moveList; // Store moves as strings
    private String currentWhiteMove; // Store current white move

    /**
     * Constructs a new ChessController.
     */
    public ChessController() {
        this.gameState = new GameState();
        this.moveCount = 0;
        this.whiteScore = 0;
        this.blackScore = 0;
        this.moveList = new ArrayList<>();
        this.currentWhiteMove = "";
    }

    /**
     * Sets the game view.
     *
     * @param view The GameWindow view
     */
    public void setView(GameWindow view) {
        this.view = view;
    }

    /**
     * Starts a new game with the specified game mode.
     *
     * @param gameMode The game mode to use
     */
    public void startNewGame(String gameMode) {
        this.gameMode = gameMode;
        gameState.resetGame();
        moveCount = 0;
        whiteScore = 0;
        blackScore = 0;
        moveList.clear();
        currentWhiteMove = "";
        updateView();

        // If playing against computer and computer goes first, make computer move
        if (gameMode.equals("Computer vs Computer") ||
                (gameMode.equals("Player vs Computer") && gameState.getCurrentTurn() == PieceColor.BLACK)) {
            makeComputerMove();
        }
    }

    /**
     * Makes a move from the source position to the target position.
     *
     * @param fromPosition The source position
     * @param toPosition The target position
     * @return True if the move was successful, false otherwise
     */
    public boolean makeMove(Position fromPosition, Position toPosition) {
        // Get the piece at the source position
        Board board = gameState.getBoard();
        Piece piece = board.getPiece(fromPosition);

        if (piece == null || piece.getColor() != gameState.getCurrentTurn()) {
            return false;
        }

        // Find a legal move that matches the from and to positions
        List<Move> legalMoves = getLegalMovesForPiece(piece);
        Move moveToMake = null;

        for (Move move : legalMoves) {
            if (move.getFrom().equals(fromPosition) && move.getTo().equals(toPosition)) {
                moveToMake = move;
                break;
            }
        }

        if (moveToMake == null) {
            return false;
        }

        // Make the move
        boolean successful = gameState.makeMove(moveToMake);

        if (successful) {
            moveCount++;
            
            // Log the move to terminal
            logMove(moveToMake, moveCount);
            
            // Update score if there was a capture
            if (moveToMake.getCapturedPiece() != null) {
                updateScore(moveToMake.getCapturedPiece());
            }
            
            updateView();

            // Check if the game is over
            if (gameState.isGameOver()) {
                logGameOver();
                view.showGameOver(gameState.getGameResult());
                return true;
            }

            // If playing against computer, make computer move
            if ((gameMode.equals("Player vs Computer") && gameState.getCurrentTurn() == PieceColor.BLACK) ||
                    gameMode.equals("Computer vs Computer")) {
                makeComputerMove();
            }
        }

        return successful;
    }

    /**
     * Makes a computer move using a simple AI.
     */
    private void makeComputerMove() {
        // Simple AI: choose a random legal move
        List<Move> legalMoves = getAllLegalMoves(gameState.getCurrentTurn());

        if (!legalMoves.isEmpty()) {
            // For now, just pick the first legal move
            // This could be improved with a proper chess AI
            Move computerMove = legalMoves.get(0);

            // Make the move after a short delay
            new Thread(() -> {
                try {
                    Thread.sleep(500); // 0.5 second delay
                    gameState.makeMove(computerMove);
                    moveCount++;
                    
                    // Log the computer move to terminal
                    logMove(computerMove, moveCount);
                    
                    // Update score if there was a capture
                    if (computerMove.getCapturedPiece() != null) {
                        updateScore(computerMove.getCapturedPiece());
                    }
                    
                    updateView();

                    // Check if the game is over
                    if (gameState.isGameOver()) {
                        logGameOver();
                        view.showGameOver(gameState.getGameResult());
                    } else if (gameMode.equals("Computer vs Computer")) {
                        // Continue with next computer move
                        makeComputerMove();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * Updates the view to reflect the current game state.
     */
    private void updateView() {
        if (view != null) {
            view.refreshBoard();

            // Update status message
            String statusMessage = gameState.getCurrentTurn().toString() + "'s turn";

            // Add check notification if applicable
            if (gameState.getBoard().isInCheck(gameState.getCurrentTurn())) {
                statusMessage += " (CHECK)";
            }

            view.updateStatus(statusMessage);
        }
    }

    /**
     * Gets the piece at the specified position.
     *
     * @param position The position to check
     * @return The piece at the position, or null if empty
     */
    public Piece getPieceAt(Position position) {
        return gameState.getBoard().getPiece(position);
    }

    /**
     * Gets all legal moves for the piece at the specified position.
     *
     * @param position The position of the piece
     * @return A list of positions representing legal moves
     */
    public List<Position> getLegalMovePositions(Position position) {
        Piece piece = getPieceAt(position);

        if (piece == null) {
            return new ArrayList<>();
        }

        List<Move> legalMoves = getLegalMovesForPiece(piece);
        List<Position> movePositions = new ArrayList<>();

        for (Move move : legalMoves) {
            movePositions.add(move.getTo());
        }

        return movePositions;
    }

    /**
     * Gets all legal moves for the specified piece.
     *
     * @param piece The piece to check
     * @return A list of legal moves
     */
    private List<Move> getLegalMovesForPiece(Piece piece) {
        return piece.getLegalMoves(gameState.getBoard());
    }

    /**
     * Gets all legal moves for the specified color.
     *
     * @param color The color to get moves for
     * @return A list of all legal moves
     */
    private List<Move> getAllLegalMoves(PieceColor color) {
        return gameState.getBoard().getLegalMoves(color);
    }

    /**
     * Forfeits the current game for the specified color.
     */
    public void surrender() {
        PieceColor currentPlayer = gameState.getCurrentTurn();
        PieceColor winner = (currentPlayer == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
        String message = winner.toString() + " wins by surrender";
        
        // Print winner
        System.out.println();
        System.out.println("Winner: " + message);
        
        // Print the complete PGN move list
        System.out.println();
        System.out.println("Complete PGN Moves:");
        for (int i = 0; i < moveList.size(); i++) {
            System.out.println((i + 1) + ". " + moveList.get(i));
        }
        
        view.showGameOver(message);
        
        // Return to main menu by closing the game window
        if (view != null) {
            view.dispose();
        }
    }

    /**
     * Gets the current board state.
     *
     * @return The chess board
     */
    public Board getBoard() {
        return gameState.getBoard();
    }

    /**
     * Gets the color of the player whose turn it is.
     *
     * @return The color of the current player
     */
    public PieceColor getCurrentTurn() {
        return gameState.getCurrentTurn();
    }

    /**
     * Checks if the game is over.
     *
     * @return True if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return gameState.isGameOver();
    }

    /**
     * Gets the current game state.
     *
     * @return The current game state
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Logs a move to the terminal in PGN format.
     */
    private void logMove(Move move, int moveNumber) {
        String pgnMove = convertMoveToPGN(move);
        
        if (move.getPiece().getColor() == PieceColor.WHITE) {
            // White's move - store it and wait for black's move
            currentWhiteMove = pgnMove;
        } else {
            // Black's move - complete the move pair and print
            String movePair = currentWhiteMove + " " + pgnMove;
            moveList.add(movePair);
            System.out.println(moveNumber + ". " + movePair);
        }
    }

    /**
     * Converts a move to PGN notation with starting position.
     */
    private String convertMoveToPGN(Move move) {
        String piece = move.getPiece().getType();
        String from = positionToString(move.getFrom());
        String to = positionToString(move.getTo());
        String result = "";
        
        // Handle different piece types
        switch (piece) {
            case "Pawn":
                // Pawns don't get a letter prefix, but include starting position
                if (move.getCapturedPiece() != null) {
                    // Capture: e4xd5 (starting position + x + destination)
                    result = from + "x" + to;
                } else {
                    // Normal pawn move: e4 (starting position + destination)
                    result = from + to;
                }
                break;
            case "Knight":
                result = "N" + from + to;
                break;
            case "Bishop":
                result = "B" + from + to;
                break;
            case "Rook":
                result = "R" + from + to;
                break;
            case "Queen":
                result = "Q" + from + to;
                break;
            case "King":
                if (move.isCastling()) {
                    // Castling notation (no starting position needed)
                    if (to.charAt(0) == 'g') {
                        result = "O-O"; // Kingside castling
                    } else {
                        result = "O-O-O"; // Queenside castling
                    }
                } else {
                    result = "K" + from + to;
                }
                break;
        }
        
        // Add capture symbol if applicable (for non-pawn pieces)
        if (move.getCapturedPiece() != null && !piece.equals("Pawn") && !move.isCastling()) {
            // Insert 'x' between starting position and destination
            result = result.substring(0, result.length() - 2) + "x" + result.substring(result.length() - 2);
        }
        
        // Add promotion if applicable
        if (move.isPromotion()) {
            result += "=" + move.getPromotionType().charAt(0);
        }
        
        return result;
    }

    /**
     * Converts a position to chess notation (e.g., "e4").
     */
    private String positionToString(Position position) {
        char file = (char) ('a' + position.getX());
        int rank = 8 - position.getY();
        return file + "" + rank;
    }

    /**
     * Logs game over information to terminal.
     */
    private void logGameOver() {
        String result = gameState.getGameResult();
        System.out.println();
        System.out.println("Winner: " + result);
        
        // Print the complete PGN move list
        System.out.println();
        System.out.println("Complete PGN Moves:");
        for (int i = 0; i < moveList.size(); i++) {
            System.out.println((i + 1) + ". " + moveList.get(i));
        }
    }

    /**
     * Updates the score based on captured piece.
     */
    private void updateScore(Piece capturedPiece) {
        int points = getPieceValue(capturedPiece);
        if (capturedPiece.getColor() == PieceColor.WHITE) {
            blackScore += points;
        } else {
            whiteScore += points;
        }
    }

    /**
     * Gets the point value of a piece.
     */
    private int getPieceValue(Piece piece) {
        switch (piece.getType()) {
            case "Pawn": return 1;
            case "Knight": return 3;
            case "Bishop": return 3;
            case "Rook": return 5;
            case "Queen": return 9;
            case "King": return 0; // King has no point value
            default: return 0;
        }
    }
}