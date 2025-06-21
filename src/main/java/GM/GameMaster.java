package GM;
import Chess.Board;
import Exceptions.ValidationResult;
import PgnAnalyzers.MoveInfo;
import Pieces.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static GM.moveValidators.*;

public class GameMaster {
    final private String[] game;
    private Board board;
    Castling whiteCastling = new Castling(), blackCastling = new Castling() ;
    private String enPassant_able;
    private final moveValidators validator = new moveValidators();
    List<String> moveErrors = new ArrayList<>();
    String moveError = "";
    String boardState = "";

    public String getMoveError() {
        return moveError;
    }

    public void setBoard(Board board){
        this.board = board;
    }
    public GameMaster(String game) {

        if(!game.isEmpty()){
            this.game = game.replaceAll("\\d+\\.\\s*", "")  // Removes '1.' or '1. ' or '1.    '
                    .trim()
                    .split("\\s+");
        }else{
            this.game = new String[0];
        }
        this.board = new Board();
    }
    public void drawBoard(){
        this.board.drawBoard();
    }
    public GameResult analyzeGame() {
        this.boardState = this.board.saveBoard();
        int turn = 0;
//       analyze each move
        for (int i = 0; i < this.game.length - 1; i++) {
            boolean isWhite = i % 2 == 0;
            turn += ((i+1)%2);
            MoveInfo move = new MoveInfo();
            move.decipherMove(this.game[i]);
//        check if move is empty
            if(move.destination.isEmpty() && !move.castlingKing && !move.castlingQueen) {
                return  new GameResult(this.game,false,this.moveError,this.board.saveBoard());
            }
            String by = (isWhite)? "White" : "Black";

            if (!analyzeMove(move,isWhite)) {
                return new GameResult(Arrays.copyOfRange(this.game,0,i+1),false,this.moveError,this.board.saveBoard());
            };

            moveErrors.clear();
            this.boardState = this.board.saveBoard();

        }
        return new GameResult(null,true,"",this.boardState);
    }

    public void updateCastlings(int row, int col,boolean isWhite,char piece){
        if (piece == 'R') {
            if (!isWhite && row == 6 && col == 0) {
                blackCastling.setQueenSide(true);
            }
            if (!isWhite && row == 6 && col == 7) {
                blackCastling.setKingSide(true);
            }
            if (isWhite && row == 0 && col == 0) {
                whiteCastling.setQueenSide(true);
            }
            if (isWhite && row == 0 && col == 7) {
                whiteCastling.setKingSide(true);
            }
        }

        if (piece == 'K') {
            if(row == 6 && col == 4){
                blackCastling.setKingMoved(true);
            }
            if(row == 0 && col == 4){
                whiteCastling.setKingMoved(true);
            }

        }

    }

    private boolean movePieces(MoveInfo move, int row, int col,String color, boolean isWhite,int destCol,int destRow){
        char piece = move.piece.charAt(0);
        Piece temp = board.getPiece(row,col);
//        check if there is promotion and if promotion can be done
        if(!move.promotion.isEmpty()){
            if(board.getPiece(destCol, destRow) == null && !move.capture){
                temp = canPromote(destRow,move,color,piece);
            }else if(board.getPiece(destCol, destRow) != null && move.capture){
                temp = canPromote(destRow,move,color,piece);
            }else{
                temp = null;
            }
            if(temp == null) return false;
        }
//        change positions of pieces
        this.board.setBoard(row, col, null);
        this.board.setBoard(destRow, destCol, temp);
//        update castling possibilities for rook and king
        if(piece == 'R' || piece == 'K'){
            if(row == 0 || row == 7){
                updateCastlings(row,col,isWhite,move.piece.charAt(0));
            }
        }
//        check if en passant is possible
        if(move.piece.charAt(0) == 'P'){
            if(move.enPassant && this.enPassant_able != null){
                int passantCol = enPassant_able.charAt(0) - 'a';
                int passantRow = enPassant_able.charAt(1) - '1';
                this.board.setBoard(passantRow, passantCol, null);
            }
            this.enPassant_able = enPassant(board,row,col,move,isWhite);
        }
        return true;
    }
    private boolean findPiece(MoveInfo move, int row, int col,String color, boolean isWhite,int destCol,int destRow) {
        char piece = move.piece.charAt(0);
        Piece[][] tempBoard = this.board.getBoard();
        Piece temp = tempBoard[row][col];
        if (temp == null) return false;
//        find if piece is on square
        if(temp.getType().charAt(0) != piece) return false;
        if (temp.getType().charAt(0) == piece) {
//            check if piece on square is piece indicated in a move
            if(!temp.getColor().equals(color)) return false;
            ValidationResult res = temp.isValidMove(board, row, col, move, isWhite,this.enPassant_able);
            if (res.isValid()) {
//                check validity for move
                if(!checkValidity(board, move, row, col, isWhite)) return false;
                return true;
            }else{
                moveErrors.add(res.getReason());
                moveError = res.getReason();
                return false;
            }
        }
        return false;
    }
    public boolean analyzeMove(MoveInfo move,boolean isWhite)  {

        String color = (isWhite)? "white" : "black";
        if (move.castlingQueen || move.castlingKing) {
            if(handleCastling(isWhite,move,color)){
                return true;
            }else{
                return false;
            }
        }
//check en passants
        if(this.enPassant_able != null && !move.enPassant){
            this.enPassant_able = null;
        }
//        if we only have known destination, check every possible square to match move
        int destRow = move.destination.charAt(1) - '1';
        int destCol = move.destination.charAt(0) - 'a';
        char piece = move.piece.charAt(0);
        boolean foundValid =false;
        if (move.location.isEmpty()) {
            Piece[][] tempBoard = this.board.getBoard();
            int countValid = 0;

            int vrow = 0;
            int vcol = 0;
            for (int row = 0; row < 8; row++) {
                for (int col = 0; col < 8; col++) {
                    if(findPiece(move,row,col,color,isWhite,destCol,destRow)) {
                        foundValid = true;
                        countValid++;
                        vrow = row;
                        vcol = col;
//                        there are multiple moves that match description
                        if(countValid > 1){
                            moveError = "more than one valid move, The notation is disambiguous";
                            return false;
                        }

                    }
                }
            }
            if(!foundValid) return false;
            movePieces(move,vrow,vcol,color,isWhite,destCol,destRow);
//            return true;

        }else if (move.location.length() == 2) {
//            we have both location of piece and destination
            int col = move.location.charAt(0) - 'a';
            int row = move.location.charAt(1) - '1';
//            check its validity
            if(findPiece(move,row,col,color,isWhite,destCol,destRow)) {
                movePieces(move,row,col,color,isWhite,destCol,destRow);
                foundValid = true;
            }
            if(!foundValid){
                moveError = "No such move for piece found: " + moveErrors.toString();
                return false;
            }

        }else {
//            we have only rank or file,
            Piece[][] tempBoard = this.board.getBoard();
            char a = move.location.charAt(0);
            int loc;
//            decide if known location is rank or file
            if (Character.isDigit(a)) {
                loc = a - '1';
            } else {
                loc = a - 'a';
            }

            for (int i = 0; i < 8; i++) {
                if(Character.isDigit(a)){
                    if(loc == destRow && i == destCol) continue;
                    if(findPiece(move,loc,i,color,isWhite,destCol,destRow)){
                        movePieces(move,loc,i,color,isWhite,destCol,destRow);
                        foundValid = true;
                        break;
                    }
                }else{
                    if(loc == destCol && i == destRow) continue;
                    if(findPiece(move,i,loc,color,isWhite,destCol,destRow)){
                        movePieces(move,i,loc,color,isWhite,destCol,destRow);
                        foundValid = true;
                        break;
                    }
                }
            }
            if(!foundValid){
                moveError = "No such move for piece found: " + moveErrors.toString();
                return false;
            }
        }
        return !isChecked(board, 0, 0, isWhite, false);
    }

    private boolean handleCastling(boolean isWhite, MoveInfo move, String color){
        int Row = isWhite ? 0 : 7;
//    check if king is checked
        if (isChecked(board,Row,4,isWhite,true)) {
            moveError = "can not do castling while checked";
            return false;
        }
//check if king can castle
        boolean castle = canCastle(this.board,(isWhite)? this.whiteCastling : this.blackCastling,color, move.castlingKing, Row);
        if(!castle) {
            moveError = "Unable to castle";
            return false;
        }
//        for queenside castling and king side castling, change positions of rooks and kings
        if(move.castlingQueen){
            Piece king = this.board.getPiece(Row,4);
            Piece rook = this.board.getPiece(Row,0);
            if(rook == null || king == null) return false;
            this.board.setBoard(Row,0 ,null);
            this.board.setBoard(Row,4,null);
            this.board.setBoard(Row,3,rook);
            this.board.setBoard(Row,2,king);
            if(isWhite){
                whiteCastling.setQueenSide(true);
                whiteCastling.setKingMoved(true);
            }else{
                blackCastling.setQueenSide(true);
                blackCastling.setKingMoved(true);
            }

            if(isChecked(this.board, Row, 2, isWhite, true)) {
                moveError = "Can not move castle at checked location";
                return false;
            }
        }else{
            Piece king = this.board.getPiece(Row,4);
            Piece rook = this.board.getPiece(Row,7);
            if(rook == null || king == null) return false;
            this.board.setBoard(Row,4 ,null);
            this.board.setBoard(Row,7,null);
            this.board.setBoard(Row,5,rook);
            this.board.setBoard(Row,6,king);
            if(isWhite){
                whiteCastling.setKingSide(true);
                whiteCastling.setKingMoved(true);
            }else{
                blackCastling.setKingSide(true);
                blackCastling.setKingMoved(true);
            }
//            check if new position leaves king valnurable
            if(isChecked(board, Row, 6, isWhite, true)){
                moveError = "Can not move castle at checked location";
                return false;
            }
        }
        return true;
    }
}
