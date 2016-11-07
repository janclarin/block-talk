package models;

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
public class User {
	/**
	 * models.User IP address. Should not change after initialization.
	 */
	private final InetAddress ipAddress;
	
	/**
	 * Port number. Should not change after initialization.
	 */
	private final int port;
	
	/**
	 * Creates a new models.User with the given IP address and port.
	 * @param ipAddress IP address.
	 * @param port Port number.
	 */
	public User(InetAddress ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	/**
	 * Gets the IP address of this models.User.
	 * @return The IP address of this models.User.
	 */
	public InetAddress getIpAddress() {
		return ipAddress;
	}
	
	/**
	 * Gets the port of this models.User.
	 * @return The port of this models.User.
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Returns a String representation of this models.User.
	 * @return String representation of this models.User.
	 */
	public String toString() {
		return String.format("IP %s Port %d", ipAddress, port);
	}
}