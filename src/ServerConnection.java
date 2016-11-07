import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerConnection implements Runnable {
	private Socket socket;
	private boolean closeConnection;
	private List<ServerConnectionListener> listeners;
	
	public ServerConnection(Socket socket, ServerConnectionListener listener){
		this.socket = socket;
		listeners = new ArrayList<ServerConnectionListener>();
		listeners.add(listener);
	}
	
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
	}
	
	public void notifyHostRequest(){
		
	}
	
	public void notifyRoomRequest(){
		
	}
	
	public void notifyUpdateHostRequest(){
		
	}
}
