package server;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import exceptions.ChatRoomNotFoundException;
import models.User;


/**
 * This class represents a head server of the system for exchanging
 * keys and IPs.
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class Server implements ClientConnectionListener
{
	/**
	 * [RoomName, Chatroom] Hash Map of all existing chatrooms on the server
	 * TODO:
	 * 	update token to desired variable
	 */
	private HashMap<String, ChatRoom> roomMap;
	
	/**
	 * Initializes the server and room map
	 */
	public Server(){
		roomMap = new HashMap<String, ChatRoom>();
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
	 * Returns List of all ChatRoom objects in existing room map. 
	 * 
	 * @return List of all ChatRooms
	 */
	public List<ChatRoom> getAllRooms(){
		Iterator<Entry<String, ChatRoom>> iterator = roomMap.entrySet().iterator();
		List<ChatRoom> allRooms = new ArrayList<ChatRoom>();
		while(iterator.hasNext()){
			allRooms.add((ChatRoom)iterator.next().getValue());
		}
		return allRooms;
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
	public void addRoomMap(String roomName, InetAddress ipAddress){
		
		roomMap.put(roomName, new ChatRoom(roomName, ipAddress));
	}
	
	/**
	 * Sends host information to requesting client
	 * 
	 */
	public void sendHost(InetAddress hostIp, InetAddress clientIp){
		
	}

	/**
	 * Authenticates token string. If roomName exists, return existing host, else create new room map and return client address.
	 * 
	 * 
	 * @param roomName
	 */
	public void authenticate(InetAddress clientIp, String roomName){
		InetAddress hostIp;
		try{
			hostIp = getRoomHost(roomName);
		}
		catch(Exception ex){
			addRoomMap(roomName, clientIp);
			hostIp = clientIp;
		}
		sendHost(hostIp, clientIp);
	}

	/**
	 * On host request, create a new room and return true if success.
	 */
	@Override
	public boolean hostRequest(User user, String roomName) {
		addRoomMap(roomName, user.getIpAddress());
		return true;
	}

	/**
	 * On room request return current list of active rooms.
	 */
	@Override
	public List<ChatRoom> roomRequest() {
		
		return getAllRooms();
	}

	/**
	 * On update host, set associated room to new host. 
	 */
	@Override
	public boolean updateHost() {
		// TODO Auto-generated method stub
		return false;
	}
}