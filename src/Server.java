import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.HashMap;

/**
 * This class represents a head server of the system for exchanging
 * keys and IPs.
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class Server
{
	private HashMap<String, ChatRoom> roomMap;
	
	/**
	 * Initializes the server and room map
	 */
	public Server(){
		roomMap = new HashMap<String, ChatRoom>();
	}
	
	/**
	 * Returns room associated with token
	 * 
	 * @param token
	 * @return
	 */
	public ChatRoom getRoom(String token){
		return roomMap.get(token);
	}
	
	/**
	 * Returns the room hosts ip Address. 
	 * 
	 * @param token
	 * @return host ip Address. 
	 */
	public InetAddress getRoomHost(String token){
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
		if(roomMap.containsKey(token)){
			hostIp = getRoomHost(token);
		}
		else{
			setRoom(token, clientIp);
			hostIp = clientIp;
		}
		sendHost(hostIp, clientIp);
	}
}