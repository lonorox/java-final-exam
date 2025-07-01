package server;

import client.NewGUI.Model.PieceColor;
import shared.Message;
import java.io.*;

public class Player {
    private String username;
    private PieceColor color;
    private ObjectOutputStream output;
    private GameSession gameSession;

    public Player(String username, ObjectOutputStream output) {
        this.username = username;
        this.output = output;
    }

    public void sendMessage(Message message) {
        try {
            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            System.err.println("Error sending message to player " + username);
        }
    }

    public String getUsername() {
        return username;
    }

    public PieceColor getColor() {
        return color;
    }

    public void setColor(PieceColor color) {
        this.color = color;
    }

    public GameSession getGameSession() {
        return gameSession;
    }

    public void setGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }
}