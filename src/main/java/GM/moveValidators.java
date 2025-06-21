package GM;

import Chess.Board;
import PgnAnalyzers.MoveInfo;
import Pieces.*;

public class moveValidators {
    public static boolean checkValidity(Board board, MoveInfo move, int row, int col, boolean isWhite) {
        int destCol = move.destination.charAt(0) - 'a';
        int destRow = move.destination.charAt(1) - '1';
        if(board.getPiece(row,col) == null) return false;
        if(move.capture){
            if(board.getPiece(destRow,destCol) == null && !move.enPassant){
                return false;
            }
            if(board.getPiece(destRow,destCol) != null){
                if(board.getPiece(destRow, destCol).getColor().equals(board.getPiece(row,col).getColor())){
                    return false;
                }
            }
            return true;
        }else{
            //there should not be anything on square, since nothing was captured according to pgn
            return board.getPiece(destRow, destCol) == null;
        }
    }

    public static Piece canPromote(int destRow, MoveInfo move, String color, char piece){
        if(piece != 'P') return null;
        boolean isWhite = color.equals("white");
        if(isWhite && destRow != 7) return null;
        if(!isWhite && destRow != 0) return null;
        char promotionPiece = move.promotion.charAt(1);
        return switch (promotionPiece) {
            case 'R' -> new Rook(color, false);
            case 'B' -> new Bishop(color, false);
            case 'N' -> new Knight(color, false);
            case 'Q' -> new Queen(color);
            default -> null;
        };

    }
    public static boolean canCastle(Board board, Castling castling, String color, boolean kingSide, int Row) {
        Piece king = board.getPiece(Row,4);
        if(king == null) return false;
//        check if king and rook are on positions
        if(!king.getType().equals("King") && !king.getColor().equals(color)) return false;
        boolean isWhite = king.getColor().equals("white");
        if(kingSide){
//            if king or rook have moved previously, castling is not possible
            if(castling.getKingSide() || castling.getKingMoved()) return false;
            Rook rook = (Rook) board.getPiece(Row,7);
            if (rook == null) return false;
            if(!rook.isKingSide()) return false;
//            for kingside castling we have rook that is on queenside
            if(!rook.getType().equals("Rook") && !rook.getColor().equals(color)) return false;
            for(int i = 5; i < 7; i++){
//                check if squares between king and rook are empty
                if(board.getPiece(Row,i) != null) return false;
                if(i <= 6){
//                    check if king passes location that might be threatened
                    if(isChecked(board,Row,i,isWhite,true)) return false;
                }
            }
        }else{
//            same as above, but foir queenside
            if(castling.getQueenSide()  || castling.getKingMoved()) return false;
            Rook rook = (Rook) board.getPiece(Row,0);
            if (rook == null) return false;
            if(rook.isKingSide()) return false;
            if(!rook.getType().equals("Rook") && !rook.getColor().equals(color)) return false;
            for(int i = 1; i < 4; i++){
                if(board.getPiece(Row,i) != null) return false;
                if(i >= 2){
                    if(isChecked(board,Row,i,isWhite,true)) return false;
                }
            }

        }
        return true;
    }

    public static String enPassant(Board board,int row, int col ,MoveInfo move, boolean isWhite){
        String dest = move.destination;
        int destCol = dest.charAt(0) - 'a';
        int destRow = dest.charAt(1) - '1';
        int dy = Math.abs(destRow - row);
        int dx = Math.abs(destCol - col);

        if(dy == 2) return dest;
        return null;
    }


    public static boolean isChecked(Board board, int row, int col, boolean isWhite, boolean kingLoc) {
        String color = isWhite ? "white" : "black";
        Piece kingPiece = new King(color);
        int kingRow = 0;
        int kingCol = 0;
        // find king
        if(!kingLoc){
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Piece piece = board.getPiece(i,j);
                    if(piece == null) continue;
                    if(piece.getType().equals("King") && piece.getColor().equals(color)){
                        kingRow = i;
                        kingCol = j;
                    }
                }
            }
        }else{
            kingRow = row;
            kingCol = col;
        }
        //check if king is pinned ( he is checked)
        return isPinned(board,kingRow,kingCol,isWhite);
    }
    public static boolean isCheckMate(Board board, int row, int col, MoveInfo move, boolean isWhite) {
        if(!isChecked(board,row,col,isWhite,true)) return false;

        int[][] moves = {{1,-1},{1,0},{1,1},{0,-1},{0,+1},{-1,-1},{-1,0},{-1,1}};
        for (int[] dir : moves){
            int rdir = row + dir[0];
            int cdir = col + dir[1];
            if(rdir >= 0 && rdir < 8 && cdir >= 0 && cdir < 8){
                if(!isChecked(board,rdir,cdir,isWhite,true)) return false;
            }
        }
        return true;
    }
    public static boolean isPinned(Board board, int row, int col, boolean isWhite) {
        String enemyColor = (isWhite) ? "black" : "white";

        /* check if king is threatened by either rook or queen vertically or horizontally */
        // vertical
        for (int i = row-1; i >= 0; i--) {
            Piece pieceRow = board.getPiece(i,col);
            if(pieceRow == null) continue;
            if (!pieceRow.getType().equals("Queen") && !pieceRow.getType().equals("Rook")) break;
            if ((pieceRow.getType().equals("Queen") || pieceRow.getType().equals("Rook")) && !pieceRow.getColor().equals(enemyColor)) break;
            return true;
        }
        for (int i = row+1; i < 8; i++) {
            Piece pieceRow = board.getPiece(i,col);
            if(pieceRow == null) continue;
            if (!pieceRow.getType().equals("Queen")  && !pieceRow.getType().equals("Rook")) break;
            if ((pieceRow.getType().equals("Queen") || pieceRow.getType().equals("Rook")) && !pieceRow.getColor().equals(enemyColor)) break;
            return true;
        }
        // horizontal
        for (int i = col-1; i >= 0; i--) {
            Piece pieceCol = board.getPiece(row,i);
            if(pieceCol == null) continue;
            if (!pieceCol.getType().equals("Queen")  && !pieceCol.getType().equals("Rook")) break;
            if ((pieceCol.getType().equals("Queen") || pieceCol.getType().equals("Rook")) && !pieceCol.getColor().equals(enemyColor)) break;
            return true;
        }
        for (int i = col+1; i < 8; i++) {
            Piece pieceCol = board.getPiece(row,i);
            if(pieceCol == null) continue;
            if (!pieceCol.getType().equals("Queen")  && !pieceCol.getType().equals("Rook")) break;
            if ((pieceCol.getType().equals("Queen") || pieceCol.getType().equals("Rook")) && !pieceCol.getColor().equals(enemyColor)) break;
            return true;
        }

        /*check if king is threatened by either bishop or queen diagonally */
        int[][] movesDiag = {{-1,-1},{-1,1},{1,1},{1,-1}};
        for (int[] dir : movesDiag) {
            int k = dir[0];
            int j = dir[1];

            int rows = row+k;
            int cols = col+j;

            while (rows >= 0 && rows < 8 && cols >= 0 && cols < 8) {
                Piece piece = board.getPiece(rows, cols);
                if(piece == null) {
                    rows += k;
                    cols += j;
                    continue;
                }

                if((isWhite && piece.getColor().equals("white")) || (!isWhite && piece.getColor().equals("black"))) break;
                if (!piece.getType().equals("Queen") && !piece.getType().equals("Bishop")) break;

                return true;

            }
        }

        /* check if king is threatened by a pawn */
        int pawnDirection = (isWhite) ? 1 : -1;
        for (int i = 0; i < 2; i++){
            int moveRow = row + pawnDirection;
            int moveCol = col  + (i % 2 == 0 ? -1 : 1);
            if(moveRow >=0 && moveRow < 8 && moveCol >= 0 && moveCol < 8){
                Piece piece = board.getPiece(moveRow,moveCol);
                if (piece != null) {
                    if(isWhite){
                        if(piece.getType().equals("Pawn") && piece.getColor().equals("black")) return true;
                    }else{
                        if(piece.getType().equals("Pawn") && piece.getColor().equals("white")) return true;
                    }
                }
            }
        }

        /* check if king is threatened by knight */
        int[][] moves = {{-2,-1},{-2,+1},{2,1},{2,-1},{1,2},{1,-2},{-1,2},{-1,-2}};
        for(int[] move :moves){
            int newRow = row + move[1];
            int newCol = col + move[0];
            if(newRow >= 0 && newRow < 8 && newCol >= 0 && newCol < 8){
                Piece piece = board.getPiece(newRow,newCol);
                if(piece == null) continue;
                if(piece.getType().equals("Knight")) return true;
            }
        }
        return false;
    }
}
