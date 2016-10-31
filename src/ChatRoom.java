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
	
	/**
	 * Initializes a chat room and sets the current host to given ip
	 * 
	 * @param ipAddress
	 */
	public ChatRoom(InetAddress ipAddress){
		hostIp = ipAddress;
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
	
}
