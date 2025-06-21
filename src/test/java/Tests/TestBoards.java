package Tests;

import Chess.Board;
import Pieces.*;

public class TestBoards {
    private Board board;
    TestBoards() {
        this.board = new Board();
    }
    public Board getBoard() {
        return board;
    }
    public final void legalCastleKingside() {
        this.board.emptyBoard();
        board.setBoard(0,4 , new King("white"));  // e1
        board.setBoard(0,7 , new Rook("white", true));  // h1
        board.setBoard(3,5 , new Pawn("white",1));
        board.setBoard(7,4 , new King("black"));  // e8
        board.setBoard(7,7 , new Rook("black", true));  // h8
        // f1 and g1 must be empty
    }
    public  final void legalKnightMove(){
        this.board.emptyBoard();
        board.setBoard(7,6 , new Knight("black",false));

    }
    public final void legalCastleQueenside() {
        this.board.emptyBoard();
        board.setBoard(7,4 , new King("black"));  // e8
        board.setBoard(7,0 , new Rook("black", false));  // a8
//        board.setBoard(3,1 , new Pawn("white",1));
        board.setBoard(3,3 , new Pawn("white",1));
//        board.setBoard(3,0 , new Pawn("white",1));
        board.setBoard(0,4 , new King("white"));  // e1
        board.setBoard(0,0 , new Rook("white", false));  // a1
        // b8, c8, d8 must be empty
    }
    public final void illegalCaptureEmptySquare() {
        this.board.emptyBoard();
        board.setBoard(0,0 , new Rook("white",true)); // White rook on a1
        board.setBoard(1,4 , new Pawn("white",1)); // White pawn on e2
        // Rook tries to capture on e5, but e5 is empty
    }

    public final void illegalCaptureOwnPiece() {
        this.board.emptyBoard();
        board.setBoard(0,1 , new Knight("white",false)); // White knight on b1
        board.setBoard(2,5 ,  new Pawn("white",2)); // White pawn on f3
        // Knight tries to capture on f3
    }

    public final void illegalRookThroughPieces() {
        this.board.emptyBoard();
        board.setBoard(7,0 , new Rook("white",true)); // White rook on a1
        board.setBoard(6,0 , new Pawn("white",1)); // White pawn blocking a2
        // Rook tries to move a1 to a8
    }

    public final void illegalCastleWhileInCheck() {
        this.board.emptyBoard();
        board.setBoard(0,4 , new King("white")); // White king on e1
        board.setBoard(0,7 , new Rook("white",true)); // White rook on h1
        board.setBoard(7,4 , new Queen("black"));  // Black queen on e8 attacking e1
        // Attempt: White castles king-side
    }

    public final void illegalCastleThroughCheck() {
        this.board.emptyBoard();
        board.setBoard(0,4 , new King("white")); // White king on e1
        board.setBoard(0,7 , new Rook("white",true)); // White rook on h1
        board.setBoard(3,5 , new Queen("black"));  // Black queen attacking f1
        // Attempt: White castles king-side
    }

    public final void illegalCastleAfterKingMoved() {
        this.board.emptyBoard();
        board.setBoard(1,4 , new King("white")); // White king (assume has moved)
        board.setBoard(0,0 , new Rook("white",false)); // White rook on a1
        // You would mark internally that the king has moved
    }

    public final void illegalKnightMove() {
        this.board.emptyBoard();
        board.setBoard(1,1 , new Knight("white",true)); // White knight on b1
        board.setBoard(4,4 , new Pawn("black",1));  // Black pawn on e5
        // Attempt: Knight tries to take e5 diagonally
    }

    public final void illegalMoveIntoCheck() {
        this.board.emptyBoard();
        board.setBoard(0,5 , new King("white")); // White king on f1
        board.setBoard(2,4 , new Queen("black"));  // Black queen on e3, attacking e2
        // Attempt: Ke2
    }

    public final void illegalPawnCaptureEmpty() {
        this.board.emptyBoard();
        board.setBoard(1,4 , new Pawn("white",1)); // White pawn on e2
        board.setBoard(2,3 , null);  // d5 is empty
        // Attempt: exd5
    }

    public final void illegalPromotionPiece() {
        this.board.emptyBoard();
        board.setBoard(0,4 , new Pawn("white",1)); // White pawn on e1
        // Attempt: e8=Z (invalid piece type)
    }
}
