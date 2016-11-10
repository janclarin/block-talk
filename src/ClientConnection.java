
import java.io.DataInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
		this.socket = socket;
		this.listener = listener;
		
	}
	
	/**
	 * Set distinct user associated with connection.
	 */
	public void setUser(){
		
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
				DataInputStream incoming = new DataInputStream(socket.getInputStream());
				String message = incoming.readUTF();
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
		if(message.startsWith("HOST")){
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
	 * Notifies listener that a client is requesting a creation of new chatroom
	 * 
	 * TODO: implement notify all active servers of new host request
	 */
	public void notifyHostRequest(String roomName){
		listener.hostRequest(getUser(), roomName);
	}
	
	/**
	 * Notifies listener that a client is requesting a list of rooms
	 * 
	 * TODO: implement notify receive list of all active rooms from servers
	 */
	public void notifyRoomRequest(){
		
	}
	
	/**
	 * Notifies listener that a room is requesting a host update
	 * 
	 * TODO: implement notify all servers of new host request for existing room
	 */
	public void notifyUpdateHostRequest(){
		
	}
}
