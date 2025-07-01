package server;

import client.NewGUI.Model.PieceColor;
import server.GM.GameMaster;
import server.GM.moveValidators;
import shared.LegacyCore.ChessGame;
import shared.Message;
import shared.PgnAnalyzers.MoveInfo;
import shared.Protocol;
import java.util.*;


public class GameSession {
    private int gameId;
    private Player whitePlayer;
    private Player blackPlayer;
    private List<String> moves = new ArrayList<>();
    private GameMaster gameMaster;
    private boolean gameOver = false;

    public GameSession(Player whitePlayer, Player blackPlayer) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.gameMaster = new GameMaster("");
        this.gameId = ChessServer.generateGameId();
    }

    public synchronized void makeMove(Player player, String moveStr) {
        if (gameOver) return;

        if ((player == whitePlayer && moves.size() % 2 != 0) ||
                (player == blackPlayer && moves.size() % 2 == 0)) {
            player.sendMessage(new Message(Protocol.ERROR, "Not your turn"));
            return;
        }

        MoveInfo move = new MoveInfo();
        try {
            move.decipherMove(moveStr);
        } catch (Exception e) {
            player.sendMessage(new Message(Protocol.ERROR, "Invalid move format"));
            return;
        }

        if (!gameMaster.analyzeMove(move, player == whitePlayer)) {
            player.sendMessage(new Message(Protocol.ERROR,
                    gameMaster.getMoveError() != null ? gameMaster.getMoveError() : "Invalid move"));
            return;
        }

        moves.add(moveStr);
        broadcast(new Message(Protocol.MOVE, moveStr));
        checkGameStatus();
    }

    private void checkGameStatus() {
        boolean isWhiteTurn = moves.size() % 2 == 0;
        boolean isInCheck = moveValidators.isChecked(gameMaster.getBoard(),
                isWhiteTurn ? 0 : 7, 4, isWhiteTurn, true);

        boolean hasLegalMoves = false;
        // TODO: Implement proper legal move checking

        if (!hasLegalMoves) {
            String result = isInCheck ?
                    (isWhiteTurn ? "0-1" : "1-0") : "1/2-1/2";
            gameOver = true;
            broadcast(new Message(Protocol.GAME_OVER, result));
            saveGameToDatabase(result);
        }
    }

    private void saveGameToDatabase(String result) {
        try {
            Map<String, String> tags = new HashMap<>();
            tags.put("Event", "Online Game");
            tags.put("White", whitePlayer.getUsername());
            tags.put("Black", blackPlayer.getUsername());
            tags.put("Result", result);

            StringBuilder pgn = new StringBuilder();
            for (int i = 0; i < moves.size(); i++) {
                if (i % 2 == 0) {
                    pgn.append((i/2 + 1) + ".").append(moves.get(i)).append(" ");
                } else {
                    pgn.append(moves.get(i)).append(" ");
                }
            }

            ChessGame game = new ChessGame(tags, pgn.toString().trim());
            ChessServer.getDbManager().saveGame(game);
        } catch (Exception e) {
            System.err.println("Failed to save game to database: " + e.getMessage());
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
            saveGameToDatabase(player.getColor() == PieceColor.WHITE ? "0-1" : "1-0");
        }
    }

    public int getGameId() {
        return gameId;
    }
}