package shared.PgnAnalyzers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import shared.LegacyCore.*;
import shared.LegacyCore.ChessGame;

public class PGNReader {
     List<ChessGame> games;

    public PGNReader() {
        games = new ArrayList<>();
    }   

    public void addGame(ChessGame game) {
        games.add(game);
    }

    public List<ChessGame> getGames() {
        return games;
    }



    private boolean isValidMovetext(String movetext) {
        // Quick fail checks for obvious errors
        String cleanedMovetext = movetext// Remove comments in curly braces// Remove variations in parentheses
                .replaceAll("\\$\\d+", "")            // Remove NAG annotations
                .replaceAll("1-0|0-1|1/2-1/2|\\*", "") // Remove game result
                .trim();

        Pattern moveGroupPattern = Pattern.compile("(\\d+)\\.\\s*(.*?)(?=\\s+\\d+\\.|$)");
        Matcher moveGroupMatcher = moveGroupPattern.matcher(cleanedMovetext);
        int expectedMoveNumber = 1;

        String validMovePattern =
                "([KQRBN]?[a-h]?[1-8]?x?[a-h][1-8](?:=[QRBN])?|O-O(?:-O)?|0-0(?:-0)?)[+#]?";

        while (moveGroupMatcher.find()) {
            // Extract current move number
            int currentMoveNumber = Integer.parseInt(moveGroupMatcher.group(1));
            String movePair = moveGroupMatcher.group(2).trim();

            // Check for missing move numbers
            if (currentMoveNumber != expectedMoveNumber) {
                return false;
            }
            String[] moves = movePair.split("\\s+");
            //at least whites should move
            if (moves.length == 0) {
                return false;
            }
            // Check for incomplete moves like
            for (String move : moves) {
                if (move.matches("[KQRBNP]?x$") || !move.matches(validMovePattern)) {
                    return false;
                }
            }
            //increment expected move number
            expectedMoveNumber++;
        }

        return true;

    }

    public void extractGames(BufferedReader file) throws IOException {
        StringBuilder movetextBuilder = new StringBuilder();
        Map<String, String> tags = new HashMap<>();
        String line = file.readLine();
        while (line != null) {
            //check if line is a tag by searching for "[" and "]"
            if (line.contains("[") && line.contains("]")) {
                // if there is a movetext, add the game to the list, and reset the movetext and tags
                if (!movetextBuilder.isEmpty()) {

                    if(isValidMovetext(movetextBuilder.toString())) {
                        String movetext = movetextBuilder.toString().trim();
                        addGame(new ChessGame(tags, movetext));
                    }
                    movetextBuilder = new StringBuilder();
                    tags = new HashMap<>();
                }
                //split tags into tag value and tag name
                String[] tag = line.split("\"");
                //store tags in a map
                tags.put(tag[0].substring(1).trim(), "\"" + tag[1] + "\"");
            }
            //if line is not a tag, it is part of the movetext
            if (!line.startsWith("[") && !line.trim().isEmpty()) {
                movetextBuilder.append(line).append(" ");
            }

            line = file.readLine();
        }

    }
    
}
