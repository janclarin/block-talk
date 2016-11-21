package server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import models.User;
import models.Message;
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
	private User user;
	private ClientConnectionListener listener;
	
	/**
	 * Initializes the server and sets the socket and listeners.
	 * A new User object is created based on socket information.
	 * 
	 * @param socket
	 * @param listener
	 */
	public ClientConnection(Socket socket, ClientConnectionListener listener){
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
	 * Set distinct user associated with connection.
	 */
	public void setUser(String username, String port){
		try{
			user = new User(username, socket.getInetAddress(), Integer.parseInt(port));
		}
		catch(Exception ex){
			System.err.println("Error: Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	/**
	 * Return distint user associated with connection
	 * @return
	 */
	public User getUser(){
		return user;
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
      
                parseMessage(message.getData());
            }
			socket.close();
		}
		catch(Exception ex){
			System.err.println("Error: Exception: " + ex.getMessage());
			ex.printStackTrace();
			closeConnection = true;
		}
	}
	
	/**
	 * Parse incoming message and send distinct notify event
	 * 
	 * @param message
	 */
	public void parseMessage(String message){
		if(message.startsWith("HLO")){
			String[] messages = message.substring(4).split(" ");
			setUser(messages[0], messages[1]);
			sendMessage("YOU "+messages[0] +" "+ socket.getInetAddress() +" "+ messages[1]);
		}
		else if(message.startsWith("HST")){
			String roomName = message.substring(4);
			notifyHostRequest(roomName);
		}
		else if(message.startsWith("ROM")){
			notifyRoomRequest();
		}
		else if(message.startsWith("NHS")){
			String userInfo = message.substring(4);
			//notify new host
		}
		else if(message.startsWith("BYE")){
			closeConnection();
		}
	}
	
	/**
	 * Send generic ACK message
	 * 
	 * @param message
	 */
	public void sendMessage(String message){
		try{
			OutputStream outputStream = socket.getOutputStream();
			//String outgoing = String.format("ACK %s", message);
			String outgoing = message;
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
		String result = listener.hostRequest(getUser(), roomName);
		sendMessage("HST "+ result);
	}
	
	/**
	 * Notifies listener that a client is requesting a list of rooms
	 * 
	 */
	public void notifyRoomRequest(){
		sendRoomListMessage(listener.roomRequest(getUser()));
	}
	
	/**
	 * Notifies listener that a room is requesting a host update
	 * 
	 * TODO: implement notify all servers of new host request for existing room
	 */
	public void notifyUpdateHostRequest(){
		
	}
}
