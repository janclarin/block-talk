import java.io.DataInputStream;
import java.io.IOException;
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
	 */
	@Override
	public void run() {
		while(!closeConnection){
			try {
				DataInputStream incoming = new DataInputStream(socket.getInputStream());
				String message = incoming.readUTF();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Notifies listener that a client is requesting a creation of new chatroom
	 */
	public void notifyHostRequest(){
		
	}
	
	/**
	 * Notifies listener that a client is requesting a list of rooms
	 */
	public void notifyRoomRequest(){
		
	}
	
	/**
	 * Notifies listener that a room is requesting a host update
	 */
	public void notifyUpdateHostRequest(){
		
	}
}
