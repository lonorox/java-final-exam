package Pieces;

import Chess.Board;
import Exceptions.ValidationResult;
import PgnAnalyzers.MoveInfo;

public abstract class Piece {
    final private String color;
    final private String type;

    public Piece(String color) {
        this.color = color;
        this.type = null;
    }

    public String getColor() {
        return color;     
    }

    public String getType() {
        return type;
    }

    public abstract String draw();

    public abstract ValidationResult isValidMove(Board board, int row, int col , MoveInfo move, boolean isWhite, String enPassant_able);

}
