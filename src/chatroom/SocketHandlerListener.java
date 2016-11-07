package chatroom;

import models.User;

/**
 *
 */
public interface SocketHandlerListener {
    void messageSent(User recipient, String message);

    void messageReceived(User sender, String message);
}
