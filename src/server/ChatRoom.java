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
	private int port;
	
	/**
	 * Initializes a chat room and sets the current host to given ip
	 * 
	 * @param ipAddress
	 */
	public ChatRoom(String name, InetAddress hostIpAddress, int port){
		hostIp = hostIpAddress;
		this.name = name;
		this.port = port;
	}
	
	/**
	 * Sets host to given ip Address + port
	 * 
	 * @param ipAddress
	 */
	public void setHost(InetAddress ipAddress, int port){
		hostIp = ipAddress;
		this.port = port;
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

	/** 
	 * Gets the current host port
	 * 
	 * @return
	 */
	public int getPort(){
		return port;
	}
	
}
