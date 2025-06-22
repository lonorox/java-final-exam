package LegacyCore;

import Exceptions.ErrorLogger;
import GM.GameMaster;
import GM.GameResult;
import PgnAnalyzers.FileHandler;
import PgnAnalyzers.PGNReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Chess{
//    read pgn file/ files
    static public BufferedReader readPgn(String path) throws IOException {
        BufferedReader z = FileHandler.readPgn(path);
        return new BufferedReader(z);
    }

    public void run(String fileName){
        PGNReader reader = new PGNReader();
        int numberOfGames = 0;
        List<Future<GameResult>> results = new ArrayList<>();
        try {
            BufferedReader file = readPgn(fileName);
            reader.extractGames(file);
            numberOfGames = reader.getGames().size();
        } catch (IOException e) {
            System.out.println("File not found");
        }

//        use threads for parallel game validations
        try (ExecutorService executor = Executors.newFixedThreadPool(
                4, r -> {
                    Thread t = new Thread(r);
                    t.setName("Game-Thread-" + t.getId());
                    return t;
                })) {

            for (ChessGame game : reader.getGames()) {
                Callable<GameResult> task = () -> {
                    GameMaster gm = new GameMaster(game.moves());
                    GameResult result = gm.analyzeGame();
                    synchronized (System.out) {
                        System.out.println("== Game validation done from Thread: " + Thread.currentThread().getName() + " ==");
                    }
                    return result;
                };
                results.add(executor.submit(task));
            }
        }

        System.out.println(" games submitted: " + results.size());
//        atomic integers so that threads can increment id's of games
        AtomicInteger counterValid = new AtomicInteger(0);
        AtomicInteger counterInvalid = new AtomicInteger(0);
        for (Future<GameResult> future : results) {
            try {
                Boolean valid = future.get().isValid(); // blocks until ready
                if (!valid) {
//                    save invalid games in errors.txt
                    ErrorLogger.log("===== "+ future.get().getGameNumber() + " ===== ");
                    String[] moves = future.get().getMoves();
                    ErrorLogger.log(Arrays.toString(moves) + " ...");
                    ErrorLogger.log(moves[moves.length - 1] + " Gives us inValid move: " + future.get().getLog());
                    ErrorLogger.log(future.get().getBoard());
                    counterInvalid.incrementAndGet();
                }else{
//                    save boards of valid games in boards.txt
                    BoardLogger.log("===== "+ future.get().getGameNumber() + " ===== ");
                    BoardLogger.log(future.get().getBoard());
                    BoardLogger.log(" ");
                    counterValid.incrementAndGet();
                }
                System.out.println("Game validity " + future.get().getGameNumber() +" --->" + valid);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Error during game validation", e);
            }
        }
//        output number of valid and invalid games
        System.out.println(" games extracted: " + numberOfGames);
        System.out.println(" Valid games: " + counterValid.get());
        System.out.println(" InValid games: " + counterInvalid.get());

    }
}