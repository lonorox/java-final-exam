package client.Network;

import shared.Message;
import shared.Protocol;

import java.io.*;
import java.net.*;

public class ChessClient {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private MessageHandler messageHandler;
    private boolean connected = false;

    public boolean isConnected() {
        return connected;
    }

    public void connect(String serverAddress, int port) throws IOException {
        try {
            socket = new Socket(serverAddress, port);
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            connected = true;

            new Thread(this::listenForMessages).start();
        } catch (IOException e) {
            connected = false;
            throw e;
        }
    }

    private void listenForMessages() {
        try {
            while (connected) {
                Message message = (Message) input.readObject();
                if (messageHandler != null) {
                    messageHandler.handle(message);
                }
            }
        } catch (Exception e) {
            connected = false;
            if (messageHandler != null) {
                messageHandler.handle(new Message(Protocol.DISCONNECT, "Disconnected from server"));
            }
        } finally {
            disconnect();
        }
    }

    public void sendMessage(Message message) {
        if (!connected) return;

        try {
            output.writeObject(message);
            output.flush();
        } catch (IOException e) {
            connected = false;
            if (messageHandler != null) {
                messageHandler.handle(new Message(Protocol.ERROR, "Connection error"));
            }
        }
    }

    public void disconnect() {
        try {
            connected = false;
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error while disconnecting: " + e.getMessage());
        }
    }

    public void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
    }
}