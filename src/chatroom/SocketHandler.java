package chatroom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Handles a connection to a chat room socket.
 * Should notify everything else aside from new connections.
 */
public class SocketHandler implements Runnable {
	/**
	 * Socket to manage.
	 */
	private final Socket socket;

    /**
     * Listener to notify.
     */
    private final SocketHandlerListener listener;

    private boolean continueRunning = true;

	/**
	 * Constructs a SocketHandler with a given Socket.
	 * @param socket The socket to manage.
	 */
	public SocketHandler(final Socket socket, final SocketHandlerListener listener) {
		this.socket = socket;
		this.listener = listener;
	}

	@Override
	public void run() {
		BufferedReader incomingStream = null;
		try {
			incomingStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		String message;
		try {
			while (continueRunning && (message = incomingStream.readLine()) != null) {
                //notifyMessageReceived(message);
            }
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
            // TODO: Handle when readLine() throws.
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message through the socket.
     * @param message The message to send.
     * @throws IOException Thrown when the connection has closed and cannot send.
     */
	public void sendMessage(String message) throws IOException {
        if (isConnectionClosed()) {
            throw new IOException("Connection has been terminated.");
        }

		try {
            PrintWriter outgoingStream = new PrintWriter(socket.getOutputStream());
			outgoingStream.println(message);
			outgoingStream.flush();
            // TODO: Handle when the connection terminates.
			//notifyMessageSent(message); // Notify listeners that the message was successfully sent.
		} catch (IOException e) {
			System.err.println("IOException: " + e);
			e.printStackTrace();
			// TODO: Notify listeners that sending the message failed.
		}
    }

    /**
     * Indicates whether or not the socket has been closed or is disconnected.
     * @return Boolean indicating if the socket has been closed or disconnected.
     */
    public boolean isConnectionClosed() {
        return !socket.isConnected() || socket.isClosed();
    }
}
