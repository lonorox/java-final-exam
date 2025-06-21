package Chess;

import java.util.Map;

public record ChessGame(Map<String, String> tags, String moves) {
    public ChessGame {
        tags = Map.copyOf(tags); // ensures immutability
    }
}

