package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
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
	 * Receives messages and handles their input
	 * 
	 * TODO: handle the recieved message
	 */
	@Override
	public void run() {
		try{
			while(!closeConnection){
				DataInputStream inBuffer = new DataInputStream(socket.getInputStream());
				String message = inBuffer.readUTF();
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
		if(message.startsWith("HELLO")){
			String username = message.substring(6);
			setUser(username);
			sendMessage(username);
		}
		else if(message.startsWith("HOST")){
			String roomName = message.substring(5);
			notifyHostRequest(roomName);
		}
		else if(message.startsWith("ROOM")){
			notifyRoomRequest();
		}
		else if(message.startsWith("NEWHOST")){
			String userInfo = message.substring(8);
			//notify new host
		}
	}
	
	/**
	 * Send generic ACK message
	 * 
	 * @param message
	 */
	public void sendMessage(String message){
		try{
			DataOutputStream outBuffer = new DataOutputStream (socket.getOutputStream());
			String outgoing = String.format("ACK %s\n", message);
			outBuffer.writeUTF(outgoing);
			outBuffer.flush();
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
		String roomList = "";
		for (ChatRoom room : rooms){
			roomList += room.getName() + "\t";
		}
		sendMessage(roomList);
	}
	
	/**
	 * Notifies listener that a client is requesting a creation of new chatroom
	 * 
	 * TODO: implement notify all active servers of new host request
	 */
	public void notifyHostRequest(String roomName){
		boolean result = listener.hostRequest(getUser(), roomName);
		sendMessage(String.valueOf(result));
	}
	
	/**
	 * Notifies listener that a client is requesting a list of rooms
	 * 
	 * TODO: implement notify receive list of all active rooms from servers
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
