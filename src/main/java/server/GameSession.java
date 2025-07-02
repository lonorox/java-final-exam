package server;

import client.NewGUI.Model.PieceColor;
import server.Database.DatabaseManager;
import server.GM.GameMaster;
import server.GM.moveValidators;
import shared.LegacyCore.ChessGame;
import shared.Message;
import shared.PgnAnalyzers.MoveInfo;
import shared.Protocol;
import Pieces.Piece;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class GameSession {
    private static final DatabaseManager dbManager = new DatabaseManager();
    private int gameId;
    private Player whitePlayer;
    private Player blackPlayer;
    private List<String> moves = new ArrayList<>();
    private GameMaster gameMaster;
    private boolean gameOver = false;
    private boolean gameSaved = false;

    public GameSession(Player whitePlayer, Player blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.gameMaster = new GameMaster("");
        this.gameId = ChessServer.generateGameId();
    }

    public synchronized void makeMove(Player player, String moveStr) {
        System.out.println("SERVER RECEIVED MOVE: " + moveStr + " from " + player.getUsername());
        if (gameOver) return;

        if ((player == whitePlayer && moves.size() % 2 != 0) ||
                (player == blackPlayer && moves.size() % 2 == 0)) {
            player.sendMessage(new Message(Protocol.ERROR, "Not your turn"));
            return;
        }
        // Just broadcast the move without validation for now
        moves.add(moveStr);
        System.out.println("SERVER BROADCASTING MOVE: " + moveStr);
        broadcast(new Message(Protocol.MOVE, moveStr));

    }

    private void checkGameStatus() {
        boolean isWhiteTurn = moves.size() % 2 == 0;
        boolean isInCheck = moveValidators.isChecked(gameMaster.getBoard(),
                isWhiteTurn ? 0 : 7, 4, isWhiteTurn, true);

        boolean hasLegalMoves = false;

        if (!hasLegalMoves) {
            String result = isInCheck ?
                    (isWhiteTurn ? "0-1" : "1-0") : "1/2-1/2";
            gameOver = true;
            broadcast(new Message(Protocol.GAME_OVER, result));
            if (!gameSaved) {
                try {
//                    List<String> pgnMoves = convertMovesToPGN(moves);
                    dbManager.saveGameToDatabase(result, whitePlayer.getUsername(), blackPlayer.getUsername(), moves);
                    gameSaved = true;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void broadcast(Message message) {
        whitePlayer.sendMessage(message);
        blackPlayer.sendMessage(message);
    }

    public void playerDisconnected(Player player) {
        gameOver = true;
        Player opponent = (player == whitePlayer) ? blackPlayer : whitePlayer;
        if (opponent != null) {
            opponent.sendMessage(new Message(Protocol.ERROR, "Opponent disconnected"));
            if (!gameSaved) {
                try {
//                    List<String> pgnMoves = convertMovesToPGN(moves);
                    dbManager.saveGameToDatabase(player.getColor() == PieceColor.WHITE ? "0-1" : "1-0", whitePlayer.getUsername(), blackPlayer.getUsername(), moves);
                    gameSaved = true;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Converts raw moves (e.g., "e2e4", "Ng1f3") to proper PGN format with capture notation
     */
    private List<String> convertMovesToPGN(List<String> rawMoves) {
        List<String> pgnMoves = new ArrayList<>();
        
        // Reset the game master board to track moves
        gameMaster = new GameMaster("");
        
        for (String rawMove : rawMoves) {
            String pgnMove = convertMoveToPGN(rawMove);
            pgnMoves.add(pgnMove);
            
            // Apply the move to the game master's board to track state
            applyMoveToGameMaster(rawMove);
        }
        
        return pgnMoves;
    }
    
    /**
     * Converts a single raw move to PGN format with capture notation
     */
    private String convertMoveToPGN(String rawMove) {
        if (rawMove == null || rawMove.length() < 4) {
            return rawMove;
        }
        
        // Handle castling
        if (rawMove.equals("e1g1") || rawMove.equals("e8g8")) {
            return "O-O";
        }
        if (rawMove.equals("e1c1") || rawMove.equals("e8c8")) {
            return "O-O-O";
        }
        
        // Parse the raw move format (e.g., "e2e4", "Ng1f3")
        String fromSquare, toSquare, pieceSymbol;
        
        if (Character.isUpperCase(rawMove.charAt(0))) {
            // Piece move like "Ng1f3"
            pieceSymbol = rawMove.substring(0, 1);
            fromSquare = rawMove.substring(1, 3);
            toSquare = rawMove.substring(3, 5);
        } else {
            // Pawn move like "e2e4"
            pieceSymbol = "";
            fromSquare = rawMove.substring(0, 2);
            toSquare = rawMove.substring(2, 4);
        }
        
        // Check if this is a capture using the board state
        boolean isCapture = isCaptureMove(fromSquare, toSquare);
        
        // Build PGN notation
        StringBuilder pgn = new StringBuilder();
        
        if (!pieceSymbol.isEmpty()) {
            pgn.append(pieceSymbol);
        }
        
        // For pawn captures, include the file of departure
        if (pieceSymbol.isEmpty() && isCapture) {
            pgn.append(fromSquare.charAt(0));
        }
        
        if (isCapture) {
            pgn.append("x");
        }
        
        pgn.append(toSquare);
        
        return pgn.toString();
    }
    
    /**
     * Determines if a move is a capture by checking the board state
     */
    private boolean isCaptureMove(String fromSquare, String toSquare) {
        try {
            // Convert chess notation to board coordinates
            int fromCol = fromSquare.charAt(0) - 'a';
            int fromRow = 8 - Character.getNumericValue(fromSquare.charAt(1));
            int toCol = toSquare.charAt(0) - 'a';
            int toRow = 8 - Character.getNumericValue(toSquare.charAt(1));
            
            // Check if there's a piece at the destination square
            Piece destPiece = gameMaster.getBoard().getPiece(toRow, toCol);
            return destPiece != null;
        } catch (Exception e) {
            // If there's any error, fall back to the file-based heuristic
            char fromFile = fromSquare.charAt(0);
            char toFile = toSquare.charAt(0);
            return fromFile != toFile;
        }
    }
    
    /**
     * Applies a move to the GameMaster's board to track the game state
     */
    private void applyMoveToGameMaster(String rawMove) {
        try {
            // Parse the move
            String fromSquare, toSquare, pieceSymbol;
            
            if (Character.isUpperCase(rawMove.charAt(0))) {
                pieceSymbol = rawMove.substring(0, 1);
                fromSquare = rawMove.substring(1, 3);
                toSquare = rawMove.substring(3, 5);
            } else {
                pieceSymbol = "";
                fromSquare = rawMove.substring(0, 2);
                toSquare = rawMove.substring(2, 4);
            }
            
            // Convert to board coordinates
            int fromCol = fromSquare.charAt(0) - 'a';
            int fromRow = 8 - Character.getNumericValue(fromSquare.charAt(1));
            int toCol = toSquare.charAt(0) - 'a';
            int toRow = 8 - Character.getNumericValue(toSquare.charAt(1));
            
            // Get the piece at the source position
            Piece piece = gameMaster.getBoard().getPiece(fromRow, fromCol);
            if (piece != null) {
                // Move the piece
                gameMaster.getBoard().setBoard(fromRow, fromCol, null);
                gameMaster.getBoard().setBoard(toRow, toCol, piece);
            }
        } catch (Exception e) {
            // Ignore errors in move application for PGN conversion
            System.err.println("Error applying move to GameMaster: " + e.getMessage());
        }
    }

    public int getGameId() {
        return gameId;
    }
}