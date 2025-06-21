package Pieces;

import Chess.Board;
import Exceptions.ValidationResult;
import PgnAnalyzers.MoveInfo;

public class Knight extends Piece {
    final private boolean KingSide;
    final private String type;

    public Knight(String color, boolean KingSide) {
        super(color);
        this.type = "Night";
        this.KingSide = KingSide;
    }

    public String getType() {
        return type;
    }

    public boolean isKingSide() {
        return KingSide;
    }

    @Override
    public String draw(){
        return (!this.getColor().equals("white")) ? "♘ " : "♞ "; // Knight
    }
    @Override
    public ValidationResult isValidMove(Board board, int row, int col, MoveInfo move, boolean isWhite, String e) {
        int destCol = move.destination.charAt(0) - 'a';
        int destRow = move.destination.charAt(1) - '1';
        // check how many squares moved horizontally and vertically
        int dy = Math.abs(destRow - row);
        int dx = Math.abs(destCol - col);

        if((dy == 2 && dx == 1) || (dy == 1 && dx == 2)){
            return ValidationResult.success();
        }else {
            boolean x = dy != 2 && dy != 1;
            boolean y = dx != 2 && dx != 1;
            String message = "Knight can only move L shape "
                    + (x ? "invalid vertical movement " : " ")
                    + (y ? "invalid horizontal movement " : " ");
            return ValidationResult.failure(message);
        }
    }
}

