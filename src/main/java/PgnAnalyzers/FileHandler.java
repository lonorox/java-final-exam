package PgnAnalyzers;

import java.io.*;
import java.nio.file.*;
public class FileHandler {
    public static BufferedReader readPgn(String path) throws IOException {
        File inputFile = new File(path);

        if (inputFile.isFile()) {
            // Single file: return reader directly
            return new BufferedReader(new FileReader(inputFile));
        } else if (inputFile.isDirectory()) {
            // Folder: read and concatenate all .pgn or .txt files into one stream
            File[] files = inputFile.listFiles((dir, name) -> name.endsWith(".pgn") || name.endsWith(".txt"));

            if (files == null || files.length == 0) {
                throw new FileNotFoundException("No PGN or TXT files found in the directory.");
            }

            // Read all contents into one string
            StringBuilder allContent = new StringBuilder();
            for (File file : files) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        allContent.append(line).append("\n");
                    }
                }
            }

            // Return a BufferedReader over the combined string
            return new BufferedReader(new StringReader(allContent.toString()));
        } else {
            throw new FileNotFoundException("Invalid path: " + path);
        }
    }
}