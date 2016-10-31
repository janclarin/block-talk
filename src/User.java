import java.net.InetAddress;

/**
 * This class represents an end user and all information
 * relevant to communicating with them, including username, 
 * IP, port, and public key.
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class User
{
	/**
	 * User IP address. Should not change after initialization.
	 */
	private final InetAddress ipAddress;
	
	/**
	 * Port number. Should not change after initialization.
	 */
	private final short port;
	
	/**
	 * Creates a new User with the given IP address and port. 
	 * @param ipAddress IP address.
	 * @param port Port number.
	 */
	public User(InetAddress ipAddress, short port)
	{
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	/**
	 * Gets the IP address of this User.
	 * @return The IP address of this User. 
	 */
	public InetAddress getIpAddress() {
		return ipAddress;
	}
	
	/**
	 * Gets the port of this User.
	 * @return The port of this User.
	 */
	public short getPort() {
		return port;
	}
}