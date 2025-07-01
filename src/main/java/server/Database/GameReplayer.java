package server.Database;

import shared.LegacyCore.Board;
import shared.LegacyCore.ChessGame;
import server.GM.GameMaster;
import java.util.List;
import java.util.ArrayList;

public class GameReplayer {
    private final DatabaseManager dbManager;
    private List<ChessGame> loadedGames;
    private int currentGameIndex;
    private GameMaster gameMaster;
    private Board board;

    public GameReplayer() {
        this.dbManager = new DatabaseManager();
        this.loadedGames = new ArrayList<>();
        this.currentGameIndex = -1;
        this.board = new Board();
    }

    public void loadGames() {
        try {
            loadedGames = dbManager.loadGames();
            currentGameIndex = -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean loadGame(int gameId) {
        try {
            String pgn = dbManager.exportGameToPGN(gameId);
            if (pgn != null) {
                // Parse the PGN and create a new game
                // This will be implemented when integrating with your PGN parser
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean nextGame() {
        if (currentGameIndex < loadedGames.size() - 1) {
            currentGameIndex++;
            initializeGame();
            return true;
        }
        return false;
    }

    public boolean previousGame() {
        if (currentGameIndex > 0) {
            currentGameIndex--;
            initializeGame();
            return true;
        }
        return false;
    }

    private void initializeGame() {
        if (currentGameIndex >= 0 && currentGameIndex < loadedGames.size()) {
            ChessGame game = loadedGames.get(currentGameIndex);
            gameMaster = new GameMaster(game.moves());
            board = new Board();
        }
    }

    public Board getCurrentBoard() {
        return board;
    }

    public String getCurrentGameInfo() {
        if (currentGameIndex >= 0 && currentGameIndex < loadedGames.size()) {
            ChessGame game = loadedGames.get(currentGameIndex);
            return String.format("Game %d: %s vs %s", 
                currentGameIndex + 1,
                game.tags().get("White"),
                game.tags().get("Black"));
        }
        return "No game loaded";
    }

    public void close() {
        dbManager.close();
    }
} 