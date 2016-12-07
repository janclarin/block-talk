package server;


import sockets.SocketHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import helpers.MessageReadHelper;
import models.messages.ByeMessage;
import models.messages.Message;
import models.messages.ProcessMessage;
import models.messages.QueueMessage;

/**
 * This class manages incoming connections to server from chatroom clients
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class ServerManager implements ClientConnectionListener {
	
	private static List<ClientConnectionListener> servers;
	private static int port = 9999;
	private static boolean listen;
	private List<Socket> serverSockets;
	private static ServerManager serverManager;

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
			System.out.println(listenServer.getInetAddress() + ":"+listenServer.getLocalPort());
			listen = true;
			
			Scanner input = new Scanner(System.in);
			System.out.println("Enter a server ip and sourcePort to connect to, or c to continue:");
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
							getInstance().addServerSocket(newServer);
							System.out.println(String.format("Server Added: %s %d", newServer.getInetAddress().toString(), newServer.getPort()));
						}
					}
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			System.out.println("Listening on "+listenServer.getInetAddress() + ":"+listenServer.getLocalPort()+"...");
			while(listen){
				try{
					threadPool.execute(new ClientConnection(listenServer.accept(), getInstance()));
				}
				catch(Exception ex){
					ex.printStackTrace();
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
	
	/**
	 * Initialize ServerManager, and serverSockets instances
	 */
	public ServerManager(){
		this.serverManager = this;
		this.serverSockets = new ArrayList<Socket>();
	}
	
	public static ServerManager getInstance(){
		return (serverManager != null) ? serverManager : new ServerManager();
	}
	
	public void removeServerSocket(Socket socket){
		synchronized(serverSockets){
			serverSockets.remove(socket);
		}
	}
	
	public void addServerSocket(Socket socket){
		synchronized(serverSockets){
			serverSockets.add(socket);
		}
	}
	
	public List<Socket> getServerSockets(){
		return serverSockets;
	}
	
    /**
     * Send messages to server sockets until a valid reply is received. Returns first message in the list.
     * 
     * @param message
     * @param messageId
     * @return
     */
    public Message sendMessageToServerSockets(Message message, UUID messageId){
    	List<Message> replies = new ArrayList<Message>();
    	List<Socket> toRemove = new ArrayList<Socket>();
		Message reply = null;
		synchronized(serverSockets){
	    	for(Socket serverSocket : serverSockets){
	    		do{
	        		try{
						reply = sendMessage(serverSocket, message);
						if(reply instanceof ProcessMessage){
							reply = ((ProcessMessage)reply).hasMessageId(messageId) ? reply : null;
						}
	        		} catch (SocketException ex){
	        			reply = new ByeMessage((InetSocketAddress) serverSocket.getLocalSocketAddress());
	        			System.out.printf("Server @%s has diconnected\n", serverSocket.getLocalSocketAddress().toString());
	        		} catch (IOException ex){
	        			reply = new ByeMessage((InetSocketAddress) serverSocket.getLocalSocketAddress());
	        			ex.printStackTrace();
	        		} catch (Exception ex) {
	        			System.out.print("how");
	        		}
	    		} while (reply == null);
	    		if(!(reply instanceof ByeMessage)){
	        		replies.add(reply);	
	    		} else {
	    			toRemove.add(serverSocket);
	    		}
	    	}
	    	for (Socket dcServer : toRemove){
	    		serverSockets.remove(dcServer);
	    	}
		}
    	return replies.iterator().next();
    }
    
    /**
     * Send message to a socket, and return reply as a message.
     * 
     * @param socket
     * @param outgoing
     * @return
     * @throws IOException
     */
    public Message sendMessage(Socket socket, Message outgoing) throws IOException {
    		InputStream serverInputStream = socket.getInputStream();
        	OutputStream serverOutputStream = socket.getOutputStream();
            serverOutputStream.write(outgoing.toByteArray());
            serverOutputStream.flush();
            return MessageReadHelper.readNextMessage(serverInputStream);
    }
    
    @Override
    public Message messageReceived(Message message) {
        Message responseMessage = null;
        UUID queueId =  UUID.randomUUID();
        responseMessage = ServerManager.getInstance().sendMessageToServerSockets(new QueueMessage(message.getSenderSocketAddress(), message, queueId), queueId);
        responseMessage = ServerManager.getInstance().sendMessageToServerSockets(new ProcessMessage(message.getSenderSocketAddress(), queueId), queueId);
        return responseMessage;
    }  
	
}
