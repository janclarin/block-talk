package chatroom;

import models.User;

/**
 *
 */
public interface SocketHandlerListener {
    void messageSent(SocketHandler recipientSocketHandler, User recipient, String message);
    void messageReceived(SocketHandler senderSocketHandler, User sender, String message);
}
