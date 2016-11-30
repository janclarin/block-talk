package models;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * This class represents an end user and all information
 * relevant to communicating with them, including username,
 * IP, sourcePort, and public key.
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
    private final InetSocketAddress socketAddress;

    /**
     * Creates a new User with the given username, IP address, and sourcePort.
     *
     * @param username Username.
     * @param socketAddress Socket address, IP + port combo.
     */
    public User(final String username, final InetSocketAddress socketAddress) {
        this.username = username;
        this.socketAddress = socketAddress;
    }

    /**
     * Gets the username of this User.
     *
     * @return The username of this User.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the Socket address of this user.
     * @return
     */
    public InetSocketAddress getSocketAddress() {
        return socketAddress;
    }

    public InetAddress getIpAddress() {
        return socketAddress.getAddress();
    }

    public int getPort() {
        return socketAddress.getPort();
    }

    /**
     * Returns a String representation of this User.
     *
     * @return String representation of this User.
     */
    public String toString() {
        return String.format("%s@%s", username, socketAddress);
    }

    /**
     * Returns a hash code of the user based only on IP and sourcePort
     *
     * @return Int the hash code of the ip and sourcePort
     */
    @Override
    public int hashCode() {
        // TODO: Should include Username in has.
        return socketAddress.hashCode();
    }

    /**
     * Compares IP and Port for equality. Username is not considered.
     *
     * @param o The object to compare for equality
     * @return Boolean True if the object o is equal to this object
     */
    @Override
    public boolean equals(Object o) {
        // TODO: Should include username in this equality check.
        if(o instanceof User){
            User user = (User)o;
            return this.socketAddress.equals(user.getSocketAddress());
        }
        return false;
    }


}