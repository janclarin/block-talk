package models;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * This class represents a chat room and contains the head user
 * and their network information
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class ChatRoom {
	private final String name;
	private InetSocketAddress hostSocketAddress;

	/**
	 * Initializes a chat room with a name and a host socket address.
	 *
	 * @param name Chat room name.
	 * @param hostSocketAddress SocketAddress of the chat room host.
	 */
	public ChatRoom(final String name, final InetSocketAddress hostSocketAddress) {
		this.name = name;
		this.hostSocketAddress = hostSocketAddress;
	}
	
	/**
	 * Sets host to given socket address.
	 * 
	 * @param hostSocketAddress SocketAddress of the chat room host.
	 */
	public void setHostSocketAddress(InetSocketAddress hostSocketAddress) {
		this.hostSocketAddress = hostSocketAddress;
	}

	/**
	 * Gets the chat room's name.
	 *
	 * @return Chat room name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the chat room host's IP address.
	 * 
	 * @return
	 */
	public InetAddress getHostIpAddress() {
		return hostSocketAddress.getAddress();
	}

	/** 
	 * Gets the chat room host's port.
	 * 
	 * @return Port of the current host.
	 */
	public int getHostPort() {
		return hostSocketAddress.getPort();
	}
}
