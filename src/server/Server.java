package server;

import exceptions.ChatRoomNotFoundException;
import exceptions.MessageTypeNotSupportedException;
import helpers.MessageReadHelper;
import models.messages.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * This class represents a head server of the system for exchanging
 * keys and IPs.
 * 
 * <p>
 * e.g. java Server sourcePort
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class Server {
	private boolean stopServer;
	private ServerSocket serverSocket;
	private Socket managerSocket;
	private int port;
	
	/**
	 * [UUID, Message] Map of stored queue messages
	 */
	private HashMap<UUID, Message> queuedMessages = new HashMap<>();
	
	/**
	 * [UUID, byte[]] Hash Map of all existing chatrooms on the server
	 */
	private HashMap<UUID, byte[]> roomMap = new HashMap<>();

	/**
	 * Main function. Requires port.
	 */
	public static void main(String[] args) throws MessageTypeNotSupportedException {
	    int port = Integer.parseInt(args[0]);
	    Server server = new Server(port);
	    server.run();
	}
	
	/**
	 * Initializes the server and room map
	 */
	public Server(int port) {
		this.port = port;
	}

	/**
	 * Runs, listens for incoming requests and responds to requests.
	 */
	public void run() throws MessageTypeNotSupportedException {
		try {
			serverSocket = new ServerSocket(port);
			managerSocket = serverSocket.accept();
			InputStream inputStream = managerSocket.getInputStream();
			while (!stopServer) {
				parseMessage(MessageReadHelper.readNextMessage(inputStream));
			}
			managerSocket.close();
			serverSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns specified room data for given token.
	 * 
	 * @param roomName
	 * @return host ip Address. 
	 */
	public byte[] getRoom(UUID token) throws ChatRoomNotFoundException{
		if(!roomMap.containsKey(token)){
			throw new ChatRoomNotFoundException(); 
		}
		return roomMap.get(token);
	}
	
	/**
	 * Add a new map to room in the hash map with token as the key.
	 * 
	 * @param roomName
	 */
	public void addRoomMap(UUID token, byte[] roomData) {
		roomMap.put(token, roomData);
	}
	
	/**
	 * Queue incoming messages
	 * 
	 * @param message
	 * @throws MessageTypeNotSupportedException
	 */
	public void parseMessage(Message message) throws MessageTypeNotSupportedException {
		if (message instanceof ProcessMessage) {
			UUID messageId = ((ProcessMessage)message).getMessageId();
			processMessage(queuedMessages.get(messageId), ((ProcessMessage)message).getMessageId());
			System.out.printf("DEBUG: Processing Message ID: %s\n", messageId.toString());
		}
		else {
			QueueMessage queueMessage = (QueueMessage)message;
			queuedMessages.put(queueMessage.getMessageId(), queueMessage.getMessage());
			sendMessage(new ProcessMessage((InetSocketAddress)serverSocket.getLocalSocketAddress(), queueMessage.getMessageId()));
			System.out.printf("DEBUG: Queued Message ID: %s\n", queueMessage.getMessageId());
		}
	}
	
	/**
	 * Process queued message
	 * 
	 * @param message
	 * @throws MessageTypeNotSupportedException
	 */
	public void processMessage(Message message, UUID token) throws MessageTypeNotSupportedException {
		InetSocketAddress serverSocketAddress = (InetSocketAddress) serverSocket.getLocalSocketAddress();
		if (message instanceof HostRoomMessage) {
			// Maps the chat room to the host room message sender's socket address.
			HostRoomMessage hostRoomMessage = (HostRoomMessage) message;
			byte[] hostData = hostRoomMessage.getRoomData();
			addRoomMap(token, hostData);
			sendMessage(new AckMessage(serverSocketAddress, String.format("TOKEN %s", token.toString())));
		} 
		else if (message instanceof RequestRoomListMessage) {
			sendMessage(new RoomListMessage(serverSocketAddress, new ArrayList<>(roomMap.values())));
		} 
		else{
			throw new MessageTypeNotSupportedException();
		}
	}
	
	
	/**
	 * Send reply message
	 * 
	 * @param message
	 */
	public void sendMessage(Message message){
		try{
			OutputStream outStream = managerSocket.getOutputStream();
			outStream.write(message.toByteArray());
			outStream.flush();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
