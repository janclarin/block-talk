package models.messages;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * This class holds all data needed for each message and formats it for
 * sending as a byte array.
 *
 * Messages follow a format of <ip><sourcePort><size><data>
 * ip = 4 bytes, source IP
 * sourcePort = 4 bytes, source sender sourcePort
 * size = 4 bytes, size of message in bytes
 * data = the data of the message
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public abstract class Message {
	/**
	 * Number of bytes in the header.
	 */
	public static final int BYTE_HEADER_SIZE = 12;

	/**
	 * The socket address of the message sender.
	 */
	protected final InetSocketAddress senderSocketAddress;

	/**
	 * Shared constructor which handles the senderSocketAddress.
	 *
	 * @param senderSocketAddress The socket address of the user.
	 */
	protected Message(final InetSocketAddress senderSocketAddress) {
		this.senderSocketAddress = senderSocketAddress;
	}

	/**
     * The data payload of the message as a String. This must be overridden by subclasses to return proper format.
	 *
	 * @return Data payload of the message as a String.
	 */
	protected abstract String getData();

    /**
     * Converts the message into its representation in bytes. Relies on getData() returning the proper protocol info.
	 *
     * @return Byte array representing the message.
     */
    public byte[] toByteArray() {
        byte[] dataBytes = getData().getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(BYTE_HEADER_SIZE + dataBytes.length);
        buffer.put(senderSocketAddress.getAddress().getAddress());
        buffer.putInt(senderSocketAddress.getPort());
        buffer.putInt(dataBytes.length);
        buffer.put(dataBytes);
        return buffer.array();
    }

	/**
	 * Constructor with split up byte array made by toByteArray method.
	 * @param header The first BYTE_HEADER_SIZE bytes of the message
	 * @param data The remaining bytes with the header removed
	public Message(byte[] header, byte[] data) throws UnknownHostException	{
		ByteBuffer bbIp = ByteBuffer.allocate(4);
		ByteBuffer bbPort = ByteBuffer.allocate(4);
		this.ip = InetAddress.getByAddress(bbIp.put(header,0,4).array());
		this.sourcePort = bbPort.put(header,4,4).getInt(0);
		this.data = new String(data);
	}
	*/

	/**
	 * Constructor with whole byte array made by toByteArray method.
	 * @param bytes A byte array representing a message made with toByteArray
	public Message(byte[] bytes) throws UnknownHostException{
		ByteBuffer bbIp = ByteBuffer.allocate(4);
		ByteBuffer bbPort = ByteBuffer.allocate(4);
		this.ip = InetAddress.getByAddress(bbIp.put(bytes,0,4).array());
		this.sourcePort = bbPort.put(bytes,4,4).getInt(0);
		byte[] data = new byte[bytes.length- BYTE_HEADER_SIZE];
		for(int i = BYTE_HEADER_SIZE; i < bytes.length; i++){
			data[i- BYTE_HEADER_SIZE] = bytes[i];
		}
		this.data = new String(data);
	}
	*/

}