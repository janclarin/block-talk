package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import models.User;

/**
 * This worker class handles a distinct connection and handles their messages to communicate
 * with main server. 
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
	 * Initializes the server and sets the socket and listener.
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
			System.exit(1);
		}
	}
	
	/**
	 * Set distinct user associated with connection.
	 */
	public void setUser(String username){
		user = new User(username, socket.getInetAddress(), socket.getPort());
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
			BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String message;
			while(!closeConnection && ((message = inputStream.readLine()) != null)){
				parseMessage(message);
			}
			socket.close();
		}
		catch(Exception ex){
			System.err.println("Error: Exception: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Parse incoming message and send distinct notify event
	 * 
	 * @param message
	 */
	public void parseMessage(String message){
		if(message.startsWith("HLO")){
			String username = message.substring(6);
			setUser(username);
			sendMessage(username);
		}
		else if(message.startsWith("HST")){
			String roomName = message.substring(5);
			notifyHostRequest(roomName);
		}
		else if(message.startsWith("ROM")){
			notifyRoomRequest();
		}
		else if(message.startsWith("NHS")){
			String userInfo = message.substring(8);
			//notify new host
		}
		else if(message.startsWith("QIT")){
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
			PrintWriter outputStream = new PrintWriter (socket.getOutputStream());
			String outgoing = String.format("ACK %s\n", message);
			outputStream.println(outgoing);
			outputStream.flush();
		}
		catch(Exception ex){
			System.err.println("Error: Exception: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Send roomlist to client
	 * 
	 * TODO: send room message using proper protocol
	 * 
	 * @param rooms
	 */
	public void sendRoomList(List<ChatRoom> rooms){
		StringBuffer roomList = new StringBuffer();
		for (ChatRoom room : rooms){
			roomList.append(room.getName());
			roomList.append("\t");
		}
		sendMessage(roomList.toString());
	}
	
	/**
	 * Notifies listener that a client is requesting a creation of new chatroom
	 * 
	 */
	public void notifyHostRequest(String roomName){
		boolean result = listener.hostRequest(getUser(), roomName);
		sendMessage(String.valueOf(result));
	}
	
	/**
	 * Notifies listener that a client is requesting a list of rooms
	 * 
	 */
	public void notifyRoomRequest(){
		List<ChatRoom> rooms = listener.roomRequest();
		sendRoomList(rooms);
	}
	
	/**
	 * Notifies listener that a room is requesting a host update
	 * 
	 * TODO: implement notify all servers of new host request for existing room
	 */
	public void notifyUpdateHostRequest(){
		
	}
}
