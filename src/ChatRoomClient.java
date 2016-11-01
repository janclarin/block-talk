import java.util.List;

/**
 * This class represents a client of the system and will
 * allow the user to create or connect to rooms and communicate
 * with the server and other clients.
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class ChatRoomClient implements BlockTalkProtocolListener {
	/**
	 * User information.
	 */
	private User user;
	
	/**
	 * A list of the other known users in the chatroom.
	 */
	private List<User> otherKnownUsers;
	
	/**
	 * Creates a new Client with the given User info.
	 * @param user Information about the User.
	 */
	public ChatRoomClient(User user) {
		this.user = user;
	}
	
	/**
	 * TODO: Sends a message to the given recipients.
	 * @param message A message as a String.
	 * @param recipients A list of users to send the message to.
	 */
	public void sendMessage(String message, List<User> recipients) {
	}
	
	/**
	 * Returns a list of other known users.
	 * @return A list of known users.
	 */
	public List<User> getOtherKnownUsers() {
		return otherKnownUsers;
	}

	/**
	 * TODO: Updates the GUI to indicate that a message has been successfully sent.
	 */
	@Override
	public void messageSent() {
	}

	/**
	 * TODO: Updates the GUI to display the received message.
	 */
	@Override
	public void messageReceived(String message) {
	}
}