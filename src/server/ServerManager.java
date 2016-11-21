package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
	private static List<Socket> serverSockets;
	
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
			serverSockets = new ArrayList<Socket>();
			
			ServerSocket listenServer = new ServerSocket(port);
			System.out.println(listenServer.getInetAddress() + ":"+listenServer.getLocalPort());
			listen = true;
			
			Scanner input = new Scanner(System.in);
			System.out.println("Enter a server ip and port to connect to, or c to continue:");
			boolean findServers = true;
			while(findServers){
				try{
					String inputLine = input.nextLine();
					if(inputLine.startsWith("c")){
						findServers = false;
					}
					else{
						String[] connectionInfo = inputLine.split(" ");
						Socket newServer = new Socket(connectionInfo[0], Integer.parseInt(connectionInfo[1]));
						if(newServer.isBound()){
							serverSockets.add(newServer);
							System.out.println(String.format("Server Added: %s %d", newServer.getInetAddress().toString(), newServer.getPort()));
						}
					}
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			while(listen){
				try{
					System.out.println("Listening on "+listenServer.getInetAddress() + ":"+listenServer.getLocalPort()+"...");
					threadPool.execute(new ClientConnection(listenServer.accept(), new ClientServerConnectionRelay(serverSockets)));
				}
				catch(Exception ex){
					ex.printStackTrace();
					continue;
				}
			}
			
			System.out.println("Closing...");
			listenServer.close();
		}
		catch(Exception ex){
			System.err.println("Error: Exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
}
