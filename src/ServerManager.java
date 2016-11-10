import java.io.IOException;
import java.net.ServerSocket;
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
	
	private static List<ServerSocket> servers;
	private static int port = 9999;
	private static boolean listen;
	
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
			servers = new ArrayList<ServerSocket>();
			ServerSocket listenServer = new ServerSocket(port);
			
			while(listen){
				//Socket activeSocket = listenServer.accept();
				for(ServerSocket server: servers){
					threadPool.execute(new ClientConnection(server.accept(), new Server()));
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
