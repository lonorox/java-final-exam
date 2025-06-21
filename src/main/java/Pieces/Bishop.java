package Pieces;

import Chess.Board;
import Exceptions.ValidationResult;
import PgnAnalyzers.MoveInfo;

public class Bishop extends Piece {
    final private boolean KingSide;
    final private String type;
    public Bishop(String color, boolean KingSide) {
        super(color);
        this.type = "Bishop";
        this.KingSide = KingSide;
    }
    public boolean isKingSide() {
        return this.KingSide;
    }

    public String getType() {
        return type;
    }

    @Override
    public String draw(){
        return (!this.getColor().equals("white")) ? "♗ " : "♝ "; // Bishop
    }
    @Override
    public ValidationResult isValidMove(Board board, int row, int col, MoveInfo move, boolean isWhite, String enPassant_able) {
        int destCol = move.destination.charAt(0) - 'a';
        int destRow = move.destination.charAt(1) - '1';
        // check how many squares moved horizontally and vertically
        int dx = Math.abs(destCol - col);
        int dy = Math.abs(destRow - row);
        if(dx == 0 || dy == 0) {
            String message = "Invalid Bishop move";
            if (enPassant_able != null) {
                if (enPassant_able.equals("queen")) {
                    message = "Invalid Queen move";
                }
            }
            return ValidationResult.failure(message);
        }; //did
      //did no move

        int stepRow = (destRow - row) / dx;
        int stepCol = (destCol - col) / dx;
        for (int i = 1; i < dx; i++) {
            if (board.getPiece(row + i * stepRow,col + i * stepCol) != null) {
                String message = "Path is blocked by another piece";
//                ErrorLogger.log(message);
                return ValidationResult.failure(message);
            }
        }

        if(dx == dy){
            return ValidationResult.success();
        }else{
            return ValidationResult.failure("can not move bishop");
        }

    }
}

