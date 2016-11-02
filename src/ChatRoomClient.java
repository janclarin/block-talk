import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This class represents a client of the system and will
 * allow the user to create or connect to rooms and communicate
 * with the server and other clients.
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class ChatRoomClient {
	/**
	 * List of listeners to be notified.
	 */
	private List<ChatRoomClientListener> listeners = new ArrayList<>();
	
	/**
	 * A list of the other known users in the chat room.
	 */
	private Set<User> knownUsers = new HashSet<>();

	/**
	 * Sockets for outgoing messages.
	 */
	private List<Socket> outgoingSockets = new LinkedList<>();

	/**
	 * Server socket for incoming messages.
	 */
	private ServerSocket incomingSocket;
	
	/**
	 * User information.
	 */
	private User user;
	
	/**
	 * Creates a new Client with the given User info.
	 * @param user Information about the User.
	 */
	public ChatRoomClient(User user) {
		this.user = user;
		// Add user to the list of known users since it should 
		// also receive the messages it sends out.
		this.knownUsers.add(user);
	}
	
	/**
	 * Returns a set of known users.
	 * @return A set of known users.
	 */
	public Set<User> getKnownUsers() {
		return knownUsers;
	}
	
	/**
	 * Registers a ChatRoomClientListener in the listeners list.
	 * @param listener The ChatRoomClientListener to register.
	 */
	public void register(ChatRoomClientListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Starts listening to incomingSocket.
	 * Replies to every received message with the port number.
	 */
	public void startListening() {
		// TODO: Move this all to another thread.
		try {
			incomingSocket = new ServerSocket(user.getPort());
			while (true) {
				Socket connection = incomingSocket.accept();

				BufferedReader incomingStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				
				// TODO: Move this later on. This is just here for testing purposes.
				notifyUserHasJoined(new User(connection.getInetAddress(), connection.getPort()));
				
				String message;
				while ((message = incomingStream.readLine()) != null) {
					notifyMessageReceived(message);
					// TODO: Remove automatic replies.
					sendMessage("Hello world, I'm " + user.getPort(), connection);
				}
				//connection.close();
			}
		} catch (IOException e) {
			System.err.println("IOException: " + e);
			e.printStackTrace();
			// TODO: Handle error.
		}
	}
	
	/**
	 * Sends a message to the given User.
	 * @param message The message as a String.
	 * @param recipientUser The recipient User.
	 */
	public void sendMessage(String message, User recipientUser) {
		try {
			Socket outgoingSocket = new Socket(recipientUser.getIpAddress(), recipientUser.getPort());
			sendMessage(message, outgoingSocket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	/**
	 * Sends a message to the given socket.
	 * @param message The message as a String.
	 * @param recipientSocket The socket of the message recipient.
	 */
	private void sendMessage(String message, Socket recipientSocket) {
		PrintWriter outgoingStream;
		try {
			// TODO: Do not create a DataOutputStream every time.
			outgoingStream = new PrintWriter(recipientSocket.getOutputStream());
			outgoingStream.println(message);
			outgoingStream.flush();
			notifyMessageSent(message); // Notify listeners that the message was successfully sent.
		} catch (IOException e) {
			System.err.println("IOException: " + e);
			e.printStackTrace();
			// TODO: Notify listeners that sending the message failed.
		}
	}
	
	/**
	 * Notifies all listeners about the sent message.
	 * @param message The sent message.
	 */
	private void notifyMessageSent(String message) {
		for (ChatRoomClientListener listener : listeners) {
			listener.messageSent(message);
		}
	}
	
	/**
	 * Notifies all listeners about the received message.
	 * @param message The received message.
	 */
	private void notifyMessageReceived(String message) {
		for (ChatRoomClientListener listener : listeners) {
			listener.messageReceived(message);
		}
	}
	
	/**
	 * Notifies all listeners about the new user who has joined.
	 * @param newUser The new user who has joined.
	 */
	private void notifyUserHasJoined(User newUser) {
		for (ChatRoomClientListener listener : listeners) {
			listener.userHasJoined(newUser);
		}
	}
}