package server;

import java.util.List;

import models.User;

/**
 * This interface is used to listen to incoming requests from a connected client
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public interface ClientConnectionListener {
	
	/**
	 * Requests a room and sets the host to the room
	 * 
	 * @return true if host request succeeded
	 */
	public boolean hostRequest(User user, String roomName);
	
	/**
	 * Request for room list.
	 * 
	 * @return List of existing rooms in map
	 */
	public List<ChatRoom> roomRequest();
	
	/**
	 * Sets new host for existing chatroom
	 * 
	 * @return true if host successfully updated
	 */
	public boolean updateHost();
}
