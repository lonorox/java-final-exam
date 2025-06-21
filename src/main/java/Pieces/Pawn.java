package Pieces;
import Chess.Board;
import Exceptions.ErrorLogger;
import Exceptions.ValidationResult;
import PgnAnalyzers.MoveInfo;
public class Pawn extends Piece {
    final private int id;
    final private String type;

    public Pawn(String color, int id) {
        super(color);
        this.id = id;
        this.type = "Pawn";
    }
    public String getType() {
        return type;
    }

    public boolean isKingSide() {
        return false;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String draw(){
        return (!this.getColor().equals("white")) ? "♙ " : "♟ "; // Pawn
    }
    @Override
    public ValidationResult isValidMove(Board board, int row, int col , MoveInfo move, boolean isWhite, String enPassant_able) {
        String dest = move.destination;
        int destCol = dest.charAt(0) - 'a';
        int destRow = dest.charAt(1) - '1';
        int dy = Math.abs(destRow - row);
        int dx = Math.abs(destCol - col);
        if(!move.capture && col != destCol) {
            return ValidationResult.failure("Pawn can move diagonally only when capturing");
        }
        if (move.enPassant){
            if(enPassant_able == null) {
                return ValidationResult.failure("Nothing can be captured using en passant");
            }
            // none passantable
//
            int passantCol = enPassant_able.charAt(0) - 'a';
            int passantRow = enPassant_able.charAt(1) - '1';
            int pdx = Math.abs(passantCol - col);
            int pdy = Math.abs(passantRow - row);
            boolean ans = isWhite ? pdy == 0 && pdx == 1 && destRow == row + 1 : pdy == 0 && destRow == row - 1 && pdx == 1;
            if(ans){
                return ValidationResult.success();
            }else{
                return ValidationResult.failure("En passant not possible");
            }
        }
        if(isWhite) {
            if (move.capture){
//                if square is not empty, check if pawn moves diagonally and if square is in reach
                if (dx == 1 && destRow == row + 1) {
                    return ValidationResult.success();
                }else{
                    return ValidationResult.failure("White pawn can only capture up diagonally");
                }
            }else{
                if(((row == 1 && destRow == 3) && board.getPiece(3,col) == null && board.getPiece(2,col) == null )|| (row + 1 == destRow)){
                    return ValidationResult.success();
                }else{
                    return ValidationResult.failure("White pawn can only move up diagonally");
                }
            }
        }else{
            if (move.capture){
                if(dx == 1 && destRow == row - 1){
                    return ValidationResult.success();
                }else{
                    return ValidationResult.failure("Black pawn can only capture down diagonally");
                }
            }else{
                if(((row == 6 && destRow == 4) && board.getPiece(4,col) == null && board.getPiece(5,col) == null)
                        || (row - 1 == destRow)){
                    return ValidationResult.success();
                }else {
                    return ValidationResult.failure("Black pawn can only move up diagonally" );
                }
            }
        }
    }
}
