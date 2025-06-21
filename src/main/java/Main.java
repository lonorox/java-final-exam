import Chess.Chess;
import PgnAnalyzers.FileHandler;
import Chess.ChessGUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ChessGUI gui = new ChessGUI();
        gui.launch();
    }
}