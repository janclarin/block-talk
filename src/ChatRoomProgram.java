import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Main class to participate in chat rooms.
 * 
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class ChatRoomProgram implements ChatRoomClientListener {
	@Override
	public void messageSent(String message) {
		System.out.printf("Successfully sent message: %s\n", message);
	}

	@Override
	public void messageReceived(String message) {
		System.out.printf("Received message: %s\n", message);
	}
	
	@Override
	public void userHasJoined(User newUser) {
		System.out.printf("New user has joined from %s\n", newUser);
	}

	/**
	 * Main function to participate in chat rooms.
	 * Must specify the following command line arguments:
	 * - Host IP address
	 * - Host port number
	 * TODO: Remove the following:
	 * Optional arguments (to connect to another client immediately (This is temporary)
	 * - Other client IP address
	 * - Other client port number
	 * 
	 * e.g. java ChatRoomProgram 127.0.0.1 5001 [127.0.0.1 5002]
	 * 
	 * @param args Command line arguments.
	 * @throws UnknownHostException Invalid host.
	 */
	public static void main(String[] args) throws UnknownHostException {
		if (args.length < 2) {
			throw new IllegalArgumentException();
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);
		User user = new User(InetAddress.getByName(host), port);
		
		// Initialize the ChatRoomClient and register this as a listener.
		ChatRoomClient client = new ChatRoomClient(user);
		client.register(new ChatRoomProgram());
		
		// Determine if we should broadcast a message to a specified user immediately.
		if (args.length == 4) {
			String otherHost = args[2];
			int otherHostPort = Integer.parseInt(args[3]);
			User otherUser = new User(InetAddress.getByName(otherHost), otherHostPort);
			String message = String.format("Sent from (%s) to (%s)", user, otherUser);
			client.sendMessage(message, otherUser);
		}
		
		// Start listening for incoming requests.
		client.startListening();
	}
}
