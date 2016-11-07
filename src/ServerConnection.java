import java.net.Socket;

public class ServerConnection implements Runnable {
	private Socket socket;
	
	public ServerConnection(Socket socket){
		this.socket = socket;
	}
	
	@Override
	public void run() {
		
	}

}
