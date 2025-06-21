package GM;

import java.util.concurrent.atomic.AtomicInteger;

public class GameResult {
    private final int gameNumber;
    private final String[] moves;
    private final boolean valid;
    private final String log;
    private final String board;
    private static final AtomicInteger counter = new AtomicInteger(0);

    public GameResult(String[] moves, boolean valid, String log,String board) {
        this.gameNumber = counter.incrementAndGet();
        this.moves = moves;
        this.valid = valid;
        this.log = log;
        this.board = board;
    }

    public int getGameNumber() { return gameNumber;}
    public String[] getMoves() { return moves; }
    public boolean isValid() { return valid; }
    public String getLog() { return log; }
    public String getBoard() { return board; }
}