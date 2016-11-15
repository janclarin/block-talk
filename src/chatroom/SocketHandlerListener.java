package chatroom;

import models.User;
import models.Message;

/**
 * Interface to be implemented by classes that should be
 * notified by an instance of SocketHandler.
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public interface SocketHandlerListener {
    void messageSent(SocketHandler recipientSocketHandler, User recipient, Message message);

    void messageReceived(SocketHandler senderSocketHandler, User sender, Message message);
}
