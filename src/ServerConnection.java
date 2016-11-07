import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerConnection implements Runnable {
	private Socket socket;
	private boolean closeConnection;
	
	public ServerConnection(Socket socket){
		this.socket = socket;
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

}
