package Chess;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class BoardLogger {
    private static final String LOG_FILE = "boards.txt";
    static {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE))) {
        } catch (IOException e) {
            System.err.println("Failed to clear board log: " + e.getMessage());
        }
    }
    public static synchronized void log(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to log board: " + e.getMessage());
        }
    }
}