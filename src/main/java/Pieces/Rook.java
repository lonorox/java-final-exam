package Pieces;

import Chess.Board;
import Exceptions.ErrorLogger;
import Exceptions.ValidationResult;
import PgnAnalyzers.MoveInfo;

public class Rook extends Piece {
    final private boolean KingSide;
    final private String type;

    public Rook(String color, boolean KingSide) {
        super(color);
        this.type = "Rook";
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
        return (!this.getColor().equals("white")) ?  "♖ " : "♜ "; // Rook
    }


    @Override
    public ValidationResult isValidMove(Board board, int row, int col, MoveInfo move, boolean isWhite, String enPassant_able) {
        int destCol = move.destination.charAt(0) - 'a';
        int destRow = move.destination.charAt(1) - '1';

        if (row != destRow && col != destCol) {
            String message = "Invalid Rook move";
            if (enPassant_able != null) {
                if (enPassant_able.equals("queen")) {
                    message = "Invalid Queen move";
                }
            }
            return ValidationResult.failure(message);

        };

        // check move direction
        int dy = Integer.signum(destRow - row);
        int dx = Integer.signum(destCol - col);
        int r = row + dy, c = col + dx;
        while (r != destRow || c != destCol) {
            if (board.getPiece(r,c) != null) {
                return ValidationResult.failure("Path is blocked by another piece");
            };
            r += dy;
            c += dx;
        }

        return ValidationResult.success();
    }
}

