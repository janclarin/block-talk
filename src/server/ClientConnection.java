package server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import models.User;
import models.messages.HelloMessage;
import models.messages.HostRoomMessage;
import models.messages.Message;
import helpers.MessageReadHelper;
import models.messages.YourInfoMessage;

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
	private User clientUser;
	private ClientConnectionListener listener;

	/**
	 * Initializes the server and sets the socket and listeners.
	 * A new User object is created based on socket information.
	 * 
	 * @param socket
	 * @param listener
	 */
	public ClientConnection(Socket socket, ClientConnectionListener listener) {
		try{
			this.socket = socket;
			this.listener = listener;
		}
		catch(Exception ex){
			System.err.println("Error: Exception: " + ex.getMessage());
			ex.printStackTrace();
			closeConnection = true;
		}
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
		try{
			InputStream inputStream = socket.getInputStream();
			Message message;
			while (!closeConnection) {
                message = MessageReadHelper.readNextMessage(inputStream);
                handleMessage(message);
            }
			socket.close();
		}
		catch(Exception ex){
			System.err.println("Error: Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void handleMessage(Message message) {
		if (message instanceof HelloMessage) {
			HelloMessage helloMessage = (HelloMessage) message;
			this.clientUser = helloMessage.getSender();

			sendMessage(new YourInfoMessage(message.getSender());
			sendMessage("YOU "+messages[0] +" "+ socket.getInetAddress() +" "+ messages[1]);
		}
		else if (message.startsWith("HST")) {
			String roomName = message.substring(4);
			notifyHostRequest(roomName);
		} else if (message.startsWith("ROM")) {
			notifyRoomRequest();
		} else if (message.startsWith("BYE")) {
			closeConnection();
		}
	}

	/**
	 * Send generic ACK message
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

	/**
	 * Send room list
	 * 
	 * @param message
	 */
	public void sendRoomListMessage(String message){
		try{
			OutputStream outputStream = socket.getOutputStream();
			String outgoing = String.format("LST %s", message);
			Message msg = new Message(socket.getInetAddress(),socket.getPort(),outgoing);
			outputStream.write(msg.toByteArray());
			outputStream.flush();
		}
		catch(Exception ex){
			System.err.println("Error: Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	/**
	 * Notifies listener that a client is requesting a creation of new chatroom
	 * 
	 */
	public void notifyHostRequest(String roomName){
		sendMessage(new HostRoomMessage(clientUser, roomName));
	}
	
	/**
	 * Notifies listener that a client is requesting a list of rooms
	 * 
	 */
	public void notifyRoomRequest(){
		sendRoomListMessage(listener.roomRequest(clientUser));
	}
	
	/**
	 * Notifies listener that a room is requesting a host update
	 * 
	 * TODO: implement notify all servers of new host request for existing room
	 */
	public void notifyUpdateHostRequest(){
		
	}
}
