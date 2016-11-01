/**
 * Interface to be implemented by classes that should be
 * notified by an instance ofBlockTalkProtocol.
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public interface BlockTalkProtocolListener {
	/**
	 * To be called when a message is sent.
	 */
	void messageSent();
	
	/**
	 * To be called when a message is received.
	 * @param message The received message.
	 */
	void messageReceived(String message);
}
