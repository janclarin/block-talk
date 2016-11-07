
public interface ServerNetworkListener {
	
	/**
	 * Requests a room and sets the host to the room
	 */
	public void HostRequest();
	
	/**
	 * Request for room list
	 */
	public void RoomRequest();
	
	
	/**
	 * Sets new host for existing chatroom
	 */
	public void UpdateHost();
}
