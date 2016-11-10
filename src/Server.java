import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	 * <Username, Chatroom> Hash Map of all existing chatrooms on the server
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
	 * Returns room associated with token. Throws null pointer if token does not exist in current map. 
	 * 
	 * @param token
	 * @return
	 */
	public ChatRoom getRoom(String token){
		if(!roomMap.containsKey(token)){
			throw new NullPointerException(); 
		}
		return roomMap.get(token);
	}
	
	/**
	 * Returns the room hosts ip Address. Throws null pointer if token does not exist in current map.
	 * 
	 * @param token
	 * @return host ip Address. 
	 */
	public InetAddress getRoomHost(String token){
		if(!roomMap.containsKey(token)){
			throw new NullPointerException(); 
		}
		return roomMap.get(token).getHost();
	}
	
	/**
	 * Sets a new room in the hash map with token as they key. 
	 * 
	 * @param token
	 * @param ipAddress
	 */
	public void setRoom(String token, InetAddress ipAddress){
		
		roomMap.put(token, new ChatRoom(ipAddress));
	}
	
	/**
	 * Sends host information to requesting client
	 * 
	 */
	public void sendHost(InetAddress hostIp, InetAddress clientIp){
		
	}

	/**
	 * Authenticates token string. If token exists, return existing host, else create new room map and return client address.
	 * 
	 * 
	 * @param token
	 */
	public void authenticate(InetAddress clientIp, String token){
		InetAddress hostIp;
		try{
			hostIp = getRoomHost(token);
		}
		catch(Exception ex){
			setRoom(token, clientIp);
			hostIp = clientIp;
		}
		sendHost(hostIp, clientIp);
	}

	@Override
	public boolean hostRequest() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<ChatRoom> roomRequest() {
		// TODO Auto-generated method stub
		return new ArrayList<ChatRoom>();
	}

	@Override
	public boolean updateHost() {
		// TODO Auto-generated method stub
		return false;
	}
}