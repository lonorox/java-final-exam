package server;

import client.NewGUI.Model.PieceColor;
import server.Database.DatabaseManager;
import shared.Message;
import shared.Protocol;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChessServer {
    private ServerSocket serverSocket;
    private List<GameSession> activeGames = new ArrayList<>();
    private List<Player> waitingPlayers = new ArrayList<>();
    private static final DatabaseManager dbManager = new DatabaseManager();
    private static int nextGameId = 1;

    public void start() {
        try {
            serverSocket = new ServerSocket(Protocol.PORT);
            System.out.println("Chess server started on port " + Protocol.PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void matchPlayers(Player player) {
        waitingPlayers.add(player);

        if (waitingPlayers.size() >= 2) {
            Player white = waitingPlayers.remove(0);
            Player black = waitingPlayers.remove(0);

            white.setColor(PieceColor.WHITE);
            black.setColor(PieceColor.BLACK);

            GameSession game = new GameSession(white, black);
            activeGames.add(game);

            white.setGameSession(game);
            black.setGameSession(game);

            white.sendMessage(new Message(Protocol.GAME_STATE, Arrays.asList("You are playing as WHITE", black.getUsername())));
            black.sendMessage(new Message(Protocol.GAME_STATE,  Arrays.asList("You are playing as BLACK", white.getUsername())));
        } else {
            player.sendMessage(new Message(Protocol.GAME_STATE, "Waiting for opponent..."));
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectInputStream input;
        private ObjectOutputStream output;
        private Player player;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                output = new ObjectOutputStream(socket.getOutputStream());
                input = new ObjectInputStream(socket.getInputStream());

                Message loginMessage = (Message) input.readObject();
                if (loginMessage.getType().equals(Protocol.LOGIN)) {
                    String username = (String) loginMessage.getContent();
                    player = new Player(username, output);
                    matchPlayers(player);
                }

                while (true) {
                    Message message = (Message) input.readObject();
                    handleMessage(message);
                }
            } catch (Exception e) {
                if (player != null) {
                    if (player.getGameSession() != null) {
                        player.getGameSession().playerDisconnected(player);
                    } else {
                        waitingPlayers.remove(player);
                    }
                }
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void handleMessage(Message message) {
            switch (message.getType()) {
                case Protocol.MOVE:
                    handleMove(message);
                    break;
                case Protocol.PGN_REQUEST:
                    handlePgnRequest(message);
                    break;
                case Protocol.CHAT:
                    handleChat(message);
                    break;
                case Protocol.LOGOUT:
                    if (player.getGameSession() != null) {
                        player.getGameSession().playerDisconnected(player);
                    }
                    break;
                default:
                    sendMessage(new Message(Protocol.ERROR, "Unknown message type"));
            }
        }

        private void handleMove(Message message) {
            if (player.getGameSession() == null) {
                sendMessage(new Message(Protocol.ERROR, "Not in a game"));
                return;
            }
            player.getGameSession().makeMove(player, (String) message.getContent());
        }

        private void handlePgnRequest(Message message) {
            try {
                int gameId = (int) message.getContent();
                String pgn = dbManager.exportGameToPGN(gameId);
                sendMessage(new Message(Protocol.PGN_RESPONSE, pgn));
            } catch (Exception e) {
                sendMessage(new Message(Protocol.ERROR, "Failed to get PGN"));
            }
        }

        private void handleChat(Message message) {
            if (player.getGameSession() != null) {
                player.getGameSession().broadcast(new Message(Protocol.CHAT,
                        player.getUsername() + ": " + message.getContent()));
            }
        }

        public void sendMessage(Message message) {
            try {
                output.writeObject(message);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized int generateGameId() {
        return nextGameId++;
    }

    public static DatabaseManager getDbManager() {
        return dbManager;
    }
}