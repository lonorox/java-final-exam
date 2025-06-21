package Pieces;

import Chess.Board;
import Exceptions.ErrorLogger;
import Exceptions.ValidationResult;
import GM.GameMaster;
import PgnAnalyzers.MoveInfo;

import static GM.moveValidators.isChecked;

public class King extends Piece {
    final private String type;
    public King(String color) {
        super(color);
        this.type = "King";
    }

    public String getType() {
        return type;
    }

    @Override
    public String draw(){
        return (!this.getColor().equals("white")) ? "♔ " : "♚ "; // King
    }
    @Override
    public ValidationResult isValidMove(Board board, int row, int col, MoveInfo move, boolean isWhite, String enPassant_able) {
        int destCol = move.destination.charAt(0) - 'a';
        int destRow = move.destination.charAt(1) - '1';
        // check how many squares moved horizontally and vertically
        int dy = Math.abs(destRow - row);
        int dx = Math.abs(destCol - col);
//        Board tempBoard = new Board();
//        tempBoard.setBoard(board.getBoard());
//        tempBoard.setBoard(destRow,destCol,null);
//        if(isChecked(board,destRow,destCol ,isWhite,true)){
//            return  ValidationResult.failure("This move leavesz king vulnerable");
//        };

        if(dy <= 1 && dx <= 1){
            return ValidationResult.success();
        }else {
            return ValidationResult.failure("Invalid king move");
        }
    }
//
//
}
