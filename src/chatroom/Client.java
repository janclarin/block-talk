package chatroom;

import models.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class represents a client of the system and will
 * allow the user to create or connect to rooms and communicate
 * with the server and other clients.
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class Client implements Runnable, SocketHandlerListener {

	/**
	 * User information.
	 */
	private final User user;

	/**
	 * Socket map for retrieving the socket handler for a models.User.
	 */
	private final Map<User, SocketHandler> userSocketHandlerMap = new HashMap<>();

	/**
	 * Thread pool.
	 */
	private ExecutorService threadPool;

	/**
	 * Listener for chat room events.
	 */
	private ClientListener listener;

	/**
	 * Server socket for incoming connections.
	 */
	private ServerSocket serverSocket;

	/**
	 * Determines if the Client should continue listening for requests.
	 */
	private boolean continueRunning = true;
	
	/**
	 * Creates a new Client with the given models.User info.
	 * @param user Information about the models.User.
	 */
	public Client(final User user, final ClientListener listener) {
		this.user = user;
		this.listener = listener;
	}

	/**
	 * Starts listening to incomingSocket. Replies to every received message with the port number.
	 */
	@Override
	public void run() {
		threadPool = Executors.newCachedThreadPool();

		try {
			serverSocket = new ServerSocket(user.getPort());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		while (continueRunning) {
			try {
				Socket socket = serverSocket.accept();
				SocketHandler socketHandler = new SocketHandler(socket, this);
				threadPool.execute(socketHandler);

                // Generate a socket from the user.
				User user = new User(socket.getInetAddress(), socket.getPort());
				userSocketHandlerMap.put(user, socketHandler);
				notifyUserHasJoined(user);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		threadPool.shutdown();
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Stops the thread from running and listening for incoming requests.
	 */
	public void stopRunning() {
		continueRunning = false;
	}

	/**
	 * Sends a message to all known recipients through their SocketHandlers.
	 * @param message The message as a String.
	 */
	public void sendMessageToAll(String message) {
		for (User recipient : userSocketHandlerMap.keySet()) {
			sendMessage(message, recipient);
		}
	}

	/**
	 * Sends a message to the given socket.
	 * @param message The message as a String.
	 * @param recipient The User to send the message to.
	 */
	public void sendMessage(String message, User recipient) {
		try {
			SocketHandler socketHandler = getUserSocketHandler(recipient);
			socketHandler.sendMessage(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Finds the SocketHandler for a User if it exists.
	 * @param user The User to find the SocketHandler for.
	 * @return The SocketHandler for a given user.
	 * @throws IOException Thrown when there is an issue creating a new Socket if the User does not have one.
	 */
	private SocketHandler getUserSocketHandler(User user) throws IOException {
        SocketHandler userSocketHandler = userSocketHandlerMap.get(user);

        if (userSocketHandler == null || userSocketHandler.isConnectionClosed()) {
            userSocketHandler = createUserSocketHandler()
		}

		return userSocketHandler;
	}

	private SocketHandler createUserSocketHandler(Socket socket) throws IOException {
        User user = new User(socket.getInetAddress(), socket.getPort());
		SocketHandler userSocketHandler = new SocketHandler(socket, this);
		userSocketHandlerMap.put(user, userSocketHandler);
        return userSocketHandler;
	}
	
	/**
	 * Notifies all listeners about the sent message.
	 * @param message The sent message.
	 */
	private void notifyMessageSent(String message) {
		listener.messageSent(message);
	}
	
	/**
	 * Notifies all listeners about the received message.
	 * @param message The received message.
	 */
	private void notifyMessageReceived(String message) {
		listener.messageReceived(message);
	}
	
	/**
	 * Notifies all listeners about the new user who has joined.
	 * @param newUser The new user who has joined.
	 */
	private void notifyUserHasJoined(User newUser) {
		listener.userHasJoined(newUser);
	}
}