import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerConnectionManager {
	
	private ServerSocket server;
	private int port = 9999;
	private boolean listen;
	
	public ServerConnectionManager(){
		ExecutorService threadPool = Executors.newCachedThreadPool();
		
		try {
			server = new ServerSocket(port);
		} 
		catch (IOException ex) {
			System.err.println("Error: Exception: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
		
		while(listen){
			try{
				threadPool.execute(new ServerConnection(server.accept()));
			}
			catch(Exception ex){
				System.err.println("Error: Exception: " + ex.getMessage());
				ex.printStackTrace();
				System.exit(1);
			}
		}
		
	}
}
