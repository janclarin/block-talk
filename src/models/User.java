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
     * Username. Should not change after initialization.
     */
    private final String username;

	/**
	 * IP address. Should not change after initialization.
	 */
	private final InetAddress ipAddress;
	
	/**
	 * Port number. Should not change after initialization.
	 */
	private final int port;
	
	/**
	 * Creates a new User with the given username, IP address, and port.
     * @param username Username.
	 * @param ipAddress IP address.
	 * @param port Port number.
	 */
	public User(String username, InetAddress ipAddress, int port) {
        this.username = username;
		this.ipAddress = ipAddress;
		this.port = port;
	}

    /**
     * Gets the username of this User.
     * @return The username of this User.
     */
	public String getUsername() {
        return username;
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
	public int getPort() {
		return port;
	}
	
	/**
	 * Returns a String representation of this User.
	 * @return String representation of this User.
	 */
	public String toString() {
		return String.format("(Username %s | IP %s | Port %d)", username, ipAddress, port);
	}
}