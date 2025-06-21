package Chess;

import GM.GameMaster;
import Pieces.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Board {
//    int[][] board;
    private Piece[][] board = new Piece[8][8];
//    private int[][] alternativeBoard = new int[8][8];
    public Board() {
//        board = new Piece[8][8];
        initialize();
    }

    public Piece[][] getBoard() {
        return this.board;
    }
    public void setBoard(Piece[][] board) {
        this.board = board;
    }
    public void setBoard(int row,int col,Piece val){
        this.board[row][col] = val;
    }
    public Piece getPiece(int row,int col){
        return this.board[row][col];
    }

    public final void initialize() {
        board[0][0] = new Rook("white", false);
        board[0][1] = new Knight("white", false);
        board[0][2] = new Bishop("white", false);
        board[0][3] = new Queen("white");
        board[0][4] = new King("white");
        board[0][5] = new Bishop("white", true);
        board[0][6] = new Knight("white", true);
        board[0][7] = new Rook("white", true);
        //initialize white Pawns
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn("white", i);
        }
        //initialize black Pieces
        board[7][0] = new Rook("black", false);
        board[7][1] = new Knight("black", false);
        board[7][2] = new Bishop("black", false);
        board[7][3] = new Queen("black");
        board[7][4] = new King("black");
        board[7][5] = new Bishop("black", true);
        board[7][6] = new Knight("black", true);
        board[7][7] = new Rook("black", true);

        //initialize black Pawns
        for (int i = 0; i < 8; i++) {
            board[6][i] = new Pawn("black", i);
        }
    }
    public void emptyBoard() {
        this.board = new Piece[8][8];
    }
    public void drawBoard() {
        for (int row = 7; row >= 0; row--) {
            System.out.print((row + 1) + " ");
            for (int col = 0; col < 8; col++) {
                Piece piece = this.board[row][col];
                if (piece == null) {
                    System.out.print(". " + " ");

                }else{
                    System.out.print(piece.draw()+ " ");
                }

            }
            System.out.println();
        }
        for (char c = 'a'; c <= 'h'; c++) {
            System.out.print("  " + c);
        }
    }

    public String saveBoard() {
        StringBuilder boardState = new StringBuilder();
        for (int row = 7; row >= 0; row--) {
            boardState.append((row + 1) + " ");
            for (int col = 0; col < 8; col++) {
                Piece piece = this.board[row][col];
                if (piece == null) {
                    boardState.append(". " + " ");
                } else {
                    boardState.append(piece.draw() + " ");
                }
            }
            boardState.append("\n");
        }

        for (char c = 'a'; c <= 'h'; c++) {
            boardState.append("  " + c);
        }
        boardState.append("\n");
        return boardState.toString();
    }
}