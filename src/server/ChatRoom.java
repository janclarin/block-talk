package server;
import java.net.InetAddress;

/**
 * This class represents a chat room and contains the head user
 * and their network information
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class ChatRoom {
	private InetAddress hostIp;
	private String name;
	
	/**
	 * Initializes a chat room and sets the current host to given ip
	 * 
	 * @param ipAddress
	 */
	public ChatRoom(String name, InetAddress hostIpAddress){
		hostIp = hostIpAddress;
		this.name = name;
	}
	
	/**
	 * Sets host to given ip Address
	 * 
	 * @param ipAddress
	 */
	public void setHost(InetAddress ipAddress){
		hostIp = ipAddress;
	}
	
	/** 
	 * Gets the current host
	 * 
	 * @return
	 */
	public InetAddress getHost(){
		return hostIp;
	}
	
	/** 
	 * Gets the current host
	 * 
	 * @return
	 */
	public String getName(){
		return name;
	}
	
}
