package chatroom;

import models.User;
import models.messages.Message;

/**
 * Interface to be implemented by classes that should be
 * notified by an instance of SocketHandler.
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public interface SocketHandlerListener {
    void messageSent(SocketHandler recipientSocketHandler, Message message);

    void messageReceived(SocketHandler senderSocketHandler, Message message);
}
