package Tests;

import server.Database.PgnDatabaseUtility;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DatabaseTest {
    private static final String TEST_PGN = "src/main/java/pgns/Carlsen.pgn";
//    private static final String TEST_PGN = "src/main/java/pgns/testDb.pgn";
    private static final String EXPORT_PGN = "exported_games.pgn";
    private static final String SINGLE_GAME_PGN = "single_game.pgn";

    public static void main(String[] args) {
        // Create a test PGN file
//        createTestPgnFile();

        // Run tests
        cleanupDB();
//        testImportAndExport();
//        testSingleGameExport();
        
        // Cleanup
//        cleanupTestFiles();
    }

    private static void createTestPgnFile() {
        try {
            String testPgnContent = """
                [Event "Test Game 1"]
                [Site "Test Site"]
                [Date "2024.03.20"]
                [Round "1"]
                [White "Player1"]
                [Black "Player2"]
                [Result "1-0"]

                1. e4 e5 2. Nf3 Nc6 3. Bb5 a6 4. Ba4 Nf6 5. O-O Be7 6. Re1 b5 7. Bb3 d6 8. c3 O-O 9. h3 Nb8 10. d4 Nbd7 1-0

                [Event "Test Game 2"]
                [Site "Test Site"]
                [Date "2024.03.20"]
                [Round "2"]
                [White "Player3"]
                [Black "Player4"]
                [Result "0-1"]

                1. d4 d5 2. c4 e6 3. Nc3 Nf6 4. Bg5 Be7 5. e3 O-O 6. Nf3 Nbd7 7. Qc2 c6 8. Bd3 dxc4 9. Bxc4 Nd5 0-1
                """;

            Files.write(Paths.get(TEST_PGN), testPgnContent.getBytes());
            System.out.println("Created test PGN file: " + TEST_PGN);
        } catch (Exception e) {
            System.err.println("Error creating test PGN file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testImportAndExport() {
        System.out.println("\n=== Testing Import and Export ===");
        PgnDatabaseUtility utility = new PgnDatabaseUtility();

        try {
            // Test import
            System.out.println("Importing games from " + TEST_PGN);
            utility.importPgnToDatabase(TEST_PGN);

            // Test export
//            System.out.println("Exporting all games to " + EXPORT_PGN);
//            utility.exportDatabaseToPgn(EXPORT_PGN);

            // Verify exported file
            File exportedFile = new File(EXPORT_PGN);
            if (exportedFile.exists()) {
                System.out.println("Successfully exported games to " + EXPORT_PGN);
                System.out.println("Exported file size: " + exportedFile.length() + " bytes");
            } else {
                System.err.println("Failed to create exported file");
            }
        } catch (Exception e) {
            System.err.println("Error during import/export test: " + e.getMessage());
            e.printStackTrace();
        } finally {
            utility.close();
        }
    }

    private static void testSingleGameExport() {
        System.out.println("\n=== Testing Single Game Export ===");
        PgnDatabaseUtility utility = new PgnDatabaseUtility();

        try {
            // Export first game
            System.out.println("Exporting game 1 to " + SINGLE_GAME_PGN);
            utility.exportGameToPgn(1, SINGLE_GAME_PGN);

            // Verify exported file
            File singleGameFile = new File(SINGLE_GAME_PGN);
            if (singleGameFile.exists()) {
                System.out.println("Successfully exported single game to " + SINGLE_GAME_PGN);
                System.out.println("Exported file size: " + singleGameFile.length() + " bytes");
                
                // Print content of exported game
                String content = new String(Files.readAllBytes(Paths.get(SINGLE_GAME_PGN)));
                System.out.println("\nExported game content:");
                System.out.println(content);
            } else {
                System.err.println("Failed to create single game export file");
            }
        } catch (Exception e) {
            System.err.println("Error during single game export test: " + e.getMessage());
            e.printStackTrace();
        } finally {
            utility.close();
        }
    }

    private static void cleanupDB() {
        PgnDatabaseUtility utility = new PgnDatabaseUtility();
        utility.deleteDb();
    }
} 