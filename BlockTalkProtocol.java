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
public class BlockTalkProtocol
{
	private Socket socket;
	private final ip;
	private final port;

	/**
	 * Constructs a protocol object
	 * @param ip The local ip to prepend to messages
	 * @param port The local port to prepend to messages
	 */
	public BlockTalkProtocol(InetAddress ip, short port)
	{
		this.ip = ip;
		this.port = port;
		socket = new Socket();
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
	 * Sends a message to the target
	 * @param data The payload of the message object to send
	 * @param targetIp The destination address
	 * @param targetPort The destination port
	 */
	public void send(String data, InetAddress targetIp, short targetPort)
	{
		Message msg = new Message(ip, port, data);
		Socket sendSock = new Socket(targetIp, targetPort);
		OutputStream out = sendSock.getOutputStream();
		out.send(msg.toByteArray());
		SendSock.close();
	}

	public Message receive()
	{
		//Somehow access a byte[] of the message
		byte[] bytes;
		Message msg = new Message(bytes);
		return msg;
	}
}