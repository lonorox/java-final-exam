package Pieces;

import shared.LegacyCore.Board;
import shared.Exceptions.ValidationResult;
import shared.PgnAnalyzers.MoveInfo;

public class Queen extends Piece {
    final private String type;
    public Queen(String color) {
        super(color);
        this.type = "Queen";
    }
    public String getType() {
        return type;
    }

    @Override
    public String draw(){
        return (!this.getColor().equals("white")) ? "♕ " : "♛ "; // Queen
    }
    @Override
    public ValidationResult isValidMove(Board board, int row, int col, MoveInfo move, boolean isWhite, String enPassant_able) {
        ValidationResult resultRook = new Rook(this.getColor(),false).isValidMove(board, row, col, move, isWhite,"Queen");
        ValidationResult resultBishop = new Bishop(this.getColor(),false).isValidMove(board, row, col, move, isWhite,"Queen");
        if(!resultRook.isValid() && !resultBishop.isValid()) return ValidationResult.failure("queen can not move");
        return ValidationResult.success();

    }
}


