import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class manages incoming connections to server from chatroom clients
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class ServerConnectionManager {
	
	private ServerSocket server;
	private int port = 9999;
	private boolean listen;
	
	/**
	 * Begins listening for incoming connections and creates a thread based on distinct connection.
	 * 
	 * @param mainServer
	 */
	public ServerConnectionManager(Server mainServer){
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
				threadPool.execute(new ServerConnection(server.accept(), mainServer));
			}
			catch(Exception ex){
				System.err.println("Error: Exception: " + ex.getMessage());
				ex.printStackTrace();
				System.exit(1);
			}
		}
		
	}
}
