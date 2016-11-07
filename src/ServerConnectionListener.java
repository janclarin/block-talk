/**
 * This interface is used to request network functions from the main server
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public interface ServerConnectionListener {
	
	/**
	 * Requests a room and sets the host to the room
	 * 
	 * @return true if host request succeeded
	 */
	public boolean HostRequest();
	
	/**
	 * Request for room list.
	 * 
	 * @return List of existing rooms in map
	 */
	public ChatRoom[] RoomRequest();
	
	/**
	 * Sets new host for existing chatroom
	 * 
	 * @return true if host successfully updated
	 */
	public boolean UpdateHost();
}
