package Tests;

import Chess.Board;
import PgnAnalyzers.MoveInfo;
import org.junit.jupiter.api.Test;
import GM.GameMaster;
import static org.junit.jupiter.api.Assertions.*;


public class UnitTests {

    private Board board;
    @Test
    public void legalKnightMove(){
        TestBoards board = new TestBoards();
        board.legalKnightMove();
        GameMaster gm = new GameMaster("");
        board.getBoard().drawBoard();
        MoveInfo move = new MoveInfo();
        move.decipherMove("Nf6");
        System.out.println();
        boolean result = gm.analyzeMove(move,false);
        assertTrue(result);

        gm.drawBoard();
    }
    @Test
    public void testLegalKingsideCastle() {
        TestBoards board = new TestBoards();
        board.legalCastleKingside();
        GameMaster gm = new GameMaster("1.O-O O-O 1-0");
        gm.setBoard(board.getBoard());
        board.getBoard().drawBoard();
        System.out.println();
        boolean result = false;
        result = gm.analyzeGame().isValid();

        board.getBoard().drawBoard();
        assertTrue(result); // Should be illegal
    }
    @Test
    public void testLegalQueensideCastle() {
        TestBoards board = new TestBoards();
        board.legalCastleQueenside();
        GameMaster gm = new GameMaster("1. O-O-O O-O-O 1-0");
        gm.setBoard(board.getBoard());
        board.getBoard().drawBoard();
        boolean result = false;

        result = gm.analyzeGame().isValid();
        board.getBoard().drawBoard();
        assertTrue(result); // Should be illegal
    }
    @Test
    public void testIllegalCaptureOwnPiece() {
        TestBoards board = new TestBoards();
        board.illegalCaptureOwnPiece();
        GameMaster gm = new GameMaster("");
        gm.setBoard(board.getBoard());
        board.getBoard().drawBoard();
        MoveInfo move = new MoveInfo();
        move.decipherMove("Nxf3");
        boolean result = false;
        result = gm.analyzeMove(move,true);
        assertFalse(result); // Should be illegal
    }

    @Test
    public void testIllegalCaptureEmptySquare() {
        TestBoards board = new TestBoards();
        board.illegalCaptureEmptySquare();
        GameMaster gm = new GameMaster("");
        gm.setBoard(board.getBoard());
        board.getBoard().drawBoard();
        MoveInfo move = new MoveInfo();
        move.decipherMove("Rxe5"); // Rook tries to capture an empty square
        boolean result = false;

        result = gm.analyzeMove(move, true);
        assertFalse(result);
    }
    @Test
    public void testIllegalCastleWhileInCheck() {
        TestBoards board = new TestBoards();
        board.illegalCastleWhileInCheck();
        GameMaster gm = new GameMaster("");
        gm.setBoard(board.getBoard());
        board.getBoard().drawBoard();
        MoveInfo move = new MoveInfo();
        move.decipherMove("O-O"); // Castling while in check
        boolean result = false;

        result = gm.analyzeMove(move, true);
        assertFalse(result);
    }

    @Test
    public void testIllegalCastleThroughCheck() {
        TestBoards board = new TestBoards();
        board.illegalCastleThroughCheck();
        GameMaster gm = new GameMaster("");
        gm.setBoard(board.getBoard());
        board.getBoard().drawBoard();
        MoveInfo move = new MoveInfo();
        move.decipherMove("O-O"); // Castling through check (f1 attacked)
        boolean result = false;

        result = gm.analyzeMove(move, true);
        assertFalse(result);
    }

    @Test
    public void testIllegalCastleAfterKingMoved() {
        TestBoards board = new TestBoards();
        board.illegalCastleAfterKingMoved();
        GameMaster gm = new GameMaster("");
        gm.setBoard(board.getBoard());
        board.getBoard().drawBoard();
        MoveInfo move = new MoveInfo();
        move.decipherMove("O-O-O"); // Castling after king moved
        boolean result = false;

        result = gm.analyzeMove(move, true);
        assertFalse(result);
    }

    @Test
    public void testIllegalKnightMove() {
        TestBoards board = new TestBoards();
        board.illegalKnightMove();
        GameMaster gm = new GameMaster("");
        gm.setBoard(board.getBoard());
        board.getBoard().drawBoard();
        MoveInfo move = new MoveInfo();
        move.decipherMove("Nxe5"); // Knight tries to move diagonally
        boolean result = false;

        result = gm.analyzeMove(move, true);
        assertFalse(result);
    }

    @Test
    public void testIllegalMoveIntoCheck() {
        TestBoards board = new TestBoards();
        board.illegalMoveIntoCheck();
        board.getBoard().drawBoard();
        GameMaster gm = new GameMaster("");
        gm.setBoard(board.getBoard());
        MoveInfo move = new MoveInfo();
        move.decipherMove("Kf2"); // King moves into check
        boolean result = false;

        result = gm.analyzeMove(move, true);
        assertFalse(result);

    }

    @Test
    public void testIllegalPawnCaptureEmpty() {
        TestBoards board = new TestBoards();
        board.illegalPawnCaptureEmpty();
        GameMaster gm = new GameMaster("");
        gm.setBoard(board.getBoard());
        board.getBoard().drawBoard();
        MoveInfo move = new MoveInfo();
        move.decipherMove("exd5"); // Pawn tries to capture empty square
        boolean result = false;

        result = gm.analyzeMove(move, true);
        assertFalse(result);
    }

    @Test
    public void testIllegalPromotionPiece() {
        TestBoards board = new TestBoards();
        board.illegalPromotionPiece();
        GameMaster gm = new GameMaster("");
        gm.setBoard(board.getBoard());
        board.getBoard().drawBoard();
        MoveInfo move = new MoveInfo();
        move.decipherMove("e8=Z"); // Invalid promotion piece
        boolean result = false;

        result = gm.analyzeMove(move, true);
        assertFalse(result);
    }
    @Test
    public void testIllegalEnPassant() {
        Board board = new Board();
        GameMaster gm = new GameMaster("1. e4 d5 2. e5 f5 3. d4 g5 4. h4 g4 5. f4 h6 6. c4 gxf3e.p. 1-0");
        boolean result = gm.analyzeGame().isValid();
        assertFalse(result);
    }
    @Test
    public void testLegalEnPassant() {
        Board board = new Board();
        GameMaster gm = new GameMaster("1. e4 d5 2. e5 f5 3. exf6ep g5 4. f4 gxf4 1-0");
        boolean result = gm.analyzeGame().isValid();
        assertTrue(result);

    }
    @Test
    public void testKnightMove(){
        Board board = new Board();
        GameMaster gm = new GameMaster("1. Nf3 Nf6 2. Nc3 Nc6 3. e4 e5 4. Nxe5 Nxe4 1-0");
        board.drawBoard();
        boolean result = gm.analyzeGame().isValid();
        gm.drawBoard();
        assertTrue(result);
    }
    @Test
    public void testBishopMove(){
        GameMaster gm = new GameMaster("1. e4 e5 2. Bc4 Bc5 3. Qh5 Nc6 4. Qxf7# 1-0");
        boolean result = gm.analyzeGame().isValid();
        gm.drawBoard();
        assertTrue(result);
    }
    @Test
    public void testRookMove(){
        Board board = new Board();
        GameMaster gm = new GameMaster("1. d4 d5 2. Nf3 Nf6 3. Bf4 e6 4. e3 Bd6 5. Bxd6 Qxd6 6. c4 c6 7. Nc3 O-O 8. Rc1 Nbd7 9. c5 Qe7 10. Bd3 e5 11. dxe5 Nxe5 12. Nxe5 Qxe5 13. O-O Re8 14. Re1 Ng4 15. g3 Qh5 1-0");
        boolean result = gm.analyzeGame().isValid();
        assertTrue(result);
    }
    @Test
    public void testQueenMove(){
        Board board = new Board();
        GameMaster gm = new GameMaster("1. d4 d5 2. c4 c6 3. Nc3 Nf6 4. Qb3 dxc4 5. Qxc4 Bf5 6. Qb3 Qb6 7. Qxb6 axb6 1-0");
        boolean result = gm.analyzeGame().isValid();
        assertTrue(result);
    }
}
