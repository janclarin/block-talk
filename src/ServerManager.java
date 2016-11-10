import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class manages incoming connections to server from chatroom clients
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class ServerManager {
	
	private static List<ClientConnectionListener> servers;
	private static int port = 9999;
	private static boolean listen;
	private static ClientConnectionListener server;
	
	/**
	 * Starts the main server manager.
	 * Begins listening for incoming connections and creates a thread based on distinct connection.
	 * 
	 * @param port
	 */
	public static void main(String[] args){
		try{
			ExecutorService threadPool = Executors.newCachedThreadPool();
			port = Integer.parseInt(args[0]);
			servers = new ArrayList<ClientConnectionListener>();
			ServerSocket listenServer = new ServerSocket(port);
			
			//use one main server for now
			servers.add(new Server());
			
			while(listen){
				for(ClientConnectionListener listener : servers){
					threadPool.execute(new ClientConnection(listenServer.accept(), listener));
				}
			}
		}
		catch(Exception ex){
			System.err.println("Error: Exception: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
}
