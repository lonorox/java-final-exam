package Exceptions;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class ErrorLogger {
    private static final String LOG_FILE = "errors.txt";
    static {
        // Clear the log file when the class is loaded
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE))) {
            // Truncates file (doesn't append)
        } catch (IOException e) {
            System.err.println("Failed to clear error log: " + e.getMessage());
        }
    }
    public static synchronized void log(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to log error: " + e.getMessage());
        }
    }
}