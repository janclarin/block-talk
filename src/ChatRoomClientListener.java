/**
 * Interface to be implemented by classes that should be
 * notified by an instance of ChatRoomClient.
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public interface ChatRoomClientListener {
	/**
	 * To be called when a message is sent.
	 */
	void messageSent(String message);
	
	/**
	 * To be called when a message is received.
	 * @param message The received message.
	 */
	void messageReceived(String message);
	
	/**
	 * To be called when a new User has joined.
	 * @param newUser The new user who has just joined.
	 */
	void userHasJoined(User newUser);
}
