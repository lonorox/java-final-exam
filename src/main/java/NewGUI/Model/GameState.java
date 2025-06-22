package NewGUI.Model;

public class GameState {
    private Board board;
    private PieceColor currentTurn;
    private boolean gameOver;
    private String gameResult;

    public GameState() {
        this.board = new Board();
        this.currentTurn = PieceColor.WHITE; // White goes first
        this.gameOver = false;
        this.gameResult = "";
    }

    public Board getBoard() {
        return board;
    }

    public PieceColor getCurrentTurn() {
        return currentTurn;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String getGameResult() {
        return gameResult;
    }

    public boolean makeMove(Move move) {
        // Check if the move is valid
        if (move.getPiece().getColor() != currentTurn) {
            return false;
        }

        // Make the move
        boolean moveSuccessful = board.makeMove(move);
        if (!moveSuccessful) {
            return false;
        }

        // Switch turns
        currentTurn = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;

        // Check for checkmate or stalemate
        checkGameEndConditions();

        return true;
    }

    private void checkGameEndConditions() {
        if (board.isCheckmate(currentTurn)) {
            gameOver = true;
            PieceColor winner = (currentTurn == PieceColor.WHITE) ? PieceColor.BLACK : PieceColor.WHITE;
            gameResult = winner.toString() + " wins by checkmate";
        } else if (board.isStalemate(currentTurn)) {
            gameOver = true;
            gameResult = "Draw by stalemate";
        }
    }

    public void resetGame() {
        this.board = new Board();
        this.currentTurn = PieceColor.WHITE;
        this.gameOver = false;
        this.gameResult = "";
    }
}
