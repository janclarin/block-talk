package server;

import exceptions.ChatRoomNotFoundException;
import helpers.MessageReadHelper;
import jdk.nashorn.internal.ir.RuntimeNode;
import models.ChatRoom;
import models.User;
import models.messages.HostRoomMessage;
import models.messages.ListRoomsMessage;
import models.messages.Message;
import models.messages.RequestRoomsMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

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
public class Server
{
	private static Server instance;
	
	private boolean stopServer;
	private Socket managerSocket;
	private int port;
	private InetAddress ip;
	
	/**
	 * [RoomName, Chatroom] Hash Map of all existing chatrooms on the server
	 */
	private HashMap<String, ChatRoom> roomMap;
	
	/**
	 * Main function. Requires ip and sourcePort to start server
	 * 
	 * @param port
	 */
	public static void main(String[] args){
		try{
			instance = new Server(InetAddress.getByName(args[0]), Integer.parseInt(args[1]));
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	/**
	 * Initializes the server and room map
	 */
	public Server(InetAddress ip, int port){
		roomMap = new HashMap<String, ChatRoom>();
		this.ip = ip;
		this.port = port;
		try {
			ServerSocket server = new ServerSocket(port);
			managerSocket = server.accept();
			InputStream inputStream = managerSocket.getInputStream();
			while(!stopServer){
				parseMessage(MessageReadHelper.readNextMessage(inputStream));
			}
			managerSocket.close();
			server.close();
		}
		catch (IOException e) {
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
		return roomMap.get(roomName).getHost();
	}
	
	/**
	 * Add a new map to room in the hash map with roomName as they key. 
	 * 
	 * @param roomName
	 * @param ipAddress
	 */
	public void addRoomMap(String roomName, InetAddress ipAddress, int port){
		
		roomMap.put(roomName, new ChatRoom(roomName, ipAddress, port));
	}
	
	/**
	 * Read incoming message and handle it
	 * 
	 * @param message
	 */
	public void parseMessage(Message message) {
		if (message instanceof HostRoomMessage) {
			HostRoomMessage hostRoomMessage = (HostRoomMessage) message;
			User host = hostRoomMessage.getSender();
			addRoomMap(hostRoomMessage.getRoomName(), host.getIpAddress(), host.getPort());
			sendMessage("Host Updated");
		} else if (message instanceof RequestRoomsMessage) {
			sendMessage(new ListRoomsMessage(roomMap.values()));
		}
	}
	
	/**
	 * Send reply message
	 * 
	 * @param message
	 */
	public void sendMessage(String message){
		try{
			OutputStream outStream = managerSocket.getOutputStream();
			String outgoing = String.format("ACK %s", message);
			Message msg = new Message(ip, port, outgoing);
			outStream.write(msg.toByteArray());
			outStream.flush();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
