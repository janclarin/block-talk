
public interface ServerConnectionListener {
	
	/**
	 * Requests a room and sets the host to the room
	 */
	public boolean HostRequest();
	
	/**
	 * Request for room list
	 */
	public ChatRoom[] RoomRequest();
	
	/**
	 * Sets new host for existing chatroom
	 */
	public boolean UpdateHost();
}
