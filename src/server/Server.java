package server;

import exceptions.ChatRoomNotFoundException;
import exceptions.MessageTypeNotSupportedException;
import helpers.MessageReadHelper;
import models.ChatRoom;
import models.messages.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
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
	 * [RoomName, Chatroom] Hash Map of all existing chatrooms on the server
	 */
	private HashMap<String, ChatRoom> roomMap = new HashMap<>();

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
	 * Returns room associated with token. Throws ChatRoomNotFoundException if token does not exist in current map. 
	 * 
	 * @param roomName
	 * @return
	 */
	public ChatRoom getRoom(String roomName) throws ChatRoomNotFoundException{
		if(!roomMap.containsKey(roomName)){
			throw new ChatRoomNotFoundException(); 
		}
		return roomMap.get(roomName);
	}

	/**
	 * Returns the room hosts ip Address. Throws ChatRoomNotFoundException if token does not exist in current map.
	 * 
	 * @param roomName
	 * @return host ip Address. 
	 */
	public InetAddress getRoomHost(String roomName) throws ChatRoomNotFoundException{
		if(!roomMap.containsKey(roomName)){
			throw new ChatRoomNotFoundException(); 
		}
		return roomMap.get(roomName).getHostIpAddress();
	}
	
	/**
	 * Add a new map to room in the hash map with roomName as they key. 
	 * 
	 * @param roomName
	 */
	public void addRoomMap(String roomName, InetSocketAddress hostSocketAddress) {
		roomMap.put(roomName, new ChatRoom(roomName, hostSocketAddress));
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
			processMessage(queuedMessages.get(messageId));
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
	public void processMessage(Message message) throws MessageTypeNotSupportedException {
		InetSocketAddress serverSocketAddress = (InetSocketAddress) serverSocket.getLocalSocketAddress();
		if (message instanceof HostRoomMessage) {
			// Maps the chat room to the host room message sender's socket address.
			HostRoomMessage hostRoomMessage = (HostRoomMessage) message;
			InetSocketAddress hostSocketAddress = hostRoomMessage.getSenderSocketAddress();
			addRoomMap(hostRoomMessage.getRoomName(), hostSocketAddress);
			sendMessage(new AckMessage(serverSocketAddress, "Host Updated"));
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
