import java.net.InetAddress;
import java.net.UnknownHostException;
import java.io.OutputStream;
import java.io.InputStream;
/**
 * This class manages the sending and receiveing of messages using
 * the prescribed format. It is intended to be used for both Client-Server
 * and P2P transmission. It also handles the encrypting and decrypting
 * of messages.
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class BlockTalkProtocol{
	private final InetAddress ip;
	private final short port;

	/**
	 * Constructs a protocol object
	 * @param ip The local ip to prepend to messages
	 * @param port The local port to prepend to messages
	 */
	public BlockTalkProtocol(InetAddress ip, short port){
		this.ip = ip;
		this.port = port;
	}

	/**
	 * @return the source IP attached to this message
	 */
	public InetAddress getIp(){return ip;}
	/**
	 * @return the source port attached to this message
	 */
	public short getPort(){return port;}

	/**
	 * Wraps a message to prepare to be sent, converting to message format and encrypting
	 * @param data The payload of the message object to send
	 */
	public byte[] wrap(String data){
		Message msg = new Message(ip, port, data);
		return msg.toByteArray();
	}

	/**
	 * Unwraps a message to prepare into a Message object, by decrypting and parsing
	 * @param bytes The received, encrypted Message object
	 */
	public Message unwrap(byte[] bytes) throws UnknownHostException{
		//Somehow access a byte[] of the message
		Message msg = new Message(bytes);
		return msg;
	}
}