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
public class ServerConnection implements Runnable {
	private Socket socket;
	private boolean closeConnection;
	private List<ServerConnectionListener> listeners;
	
	public ServerConnection(Socket socket, ServerConnectionListener listener){
		this.socket = socket;
		listeners = new ArrayList<ServerConnectionListener>();
		listeners.add(listener);
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
	 * Notifies listener that a client is requesting a creation of new chatroom
	 * 
	 * TODO: implement notify all active servers of new host request
	 */
	public void notifyHostRequest(){
		
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
