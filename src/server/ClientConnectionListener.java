package server;

import models.User;
import models.messages.Message;

/**
 * This interface is used to listen to incoming requests from a connected client
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public interface ClientConnectionListener {

    public Message messageReceived(Message message);
}