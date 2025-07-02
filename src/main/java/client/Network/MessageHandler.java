package client.Network;

import shared.Message;

public interface MessageHandler {
    void handle(Message message);
}