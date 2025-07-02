package client;


import client.NewGUI.View.GameWindow;
import client.NewGUI.Controller.ChessController;

public class ClientMain {
    public static void main(String[] args) {
        ChessController controller = new ChessController();
        GameWindow window = new GameWindow(controller);
    }
}