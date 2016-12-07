package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import models.User;
import models.messages.*;
import helpers.MessageReadHelper;

/**
 * This worker class handles a distinct connection and handles their messages to communicate
 * with the servers. 
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class ClientConnection implements Runnable {
	private Socket socket;
	private boolean closeConnection;
	private ClientConnectionListener listener;

	/**
	 * Initializes the server
	 * A new User object is created based on socket information.
	 * 
	 * @param socket
	 */
	public ClientConnection(Socket socket, ClientConnectionListener listener) {
		this.socket = socket;
		this.listener = listener;
	}
	
	/**
	 * Sets closeConnection flag to true
	 */
	public void closeConnection(){
		closeConnection = true;
	}
	
	/**
	 * Receives messages and handles their input
	 * 
	 */
	@Override
	public void run() {
		try {
			System.out.printf("New Connection @%s", socket.getInetAddress().getHostAddress());
			InputStream inputStream = socket.getInputStream();
			Message message;
			while (!closeConnection) {
                message = MessageReadHelper.readNextMessage(inputStream);
                handleMessage(message);
            }
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Handles the messages. This should really be handled by the server.
	 * @param message
	 */
	public void handleMessage(Message message) {
		if (message instanceof HelloMessage) {
			HelloMessage helloMessage = (HelloMessage) message;
			User sender = helloMessage.getSender();
			sendMessage(new YourInfoMessage((InetSocketAddress) socket.getLocalSocketAddress(), sender));
		} else if (message instanceof ByeMessage) {
			closeConnection();
		} else {
			// Forward message to listener which forwards them to servers.
			Message listenerResponse = listener.messageReceived(message);
			sendMessage(listenerResponse);
		}
	}

	/**
	 * Send a message to the client.
	 * 
	 * @param message
	 */
	public void sendMessage(Message message) {
		try{
			OutputStream outputStream = socket.getOutputStream();
			outputStream.write(message.toByteArray());
			outputStream.flush();
		}
		catch(Exception ex){
			System.err.println("Error: Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
