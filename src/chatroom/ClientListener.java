package chatroom;

import models.User;
import models.messages.Message;

/**
 * Interface to be implemented by classes that should be
 * notified by an instance of Client.
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public interface ClientListener {
    /**
     * To be called when a message is sent.
     *
     * @param recipient  The user who received the message.
     * @param message The sent message.
     */
    void messageSent(User recipient, Message message);

    /**
     * To be called when a message is received.
     *
     * @param sender  The user who sent the message.
     * @param message The received message.
     */
    void messageReceived(User sender, Message message);

    /**
     * To be called when a new models.User has joined.
     *
     * @param newUser The new user who has just joined.
     */
    void userHasJoined(User newUser);
}
