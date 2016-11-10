package models;

import java.nio.ByteBuffer;
import java.net.InetAddress;
import java.net.UnknownHostException;
/**
 * This class holds all data needed for each message and formats it for
 * sending as a byte array.
 *
 * Messages follow a format of <ip><port><size><data>
 * ip = 4 bytes, source IP
 * port = 2 bytes, source sender port
 * size = 4 bytes, size of message in bytes
 * data = the data of the message
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class Message{
	public static final int HEADER_SIZE = 10;
	private InetAddress ip;
	private short port;
	private String data;

	/**
	 * Default constructor with no initialized values.
	 */
	public Message(){}

	/**
	 * Constructor with all values set.
	 * @param ip The source IP address of this message
	 * @param port The source port of this message
	 * @param data The payload of this message
	 */
	public Message(InetAddress ip, short port, String data){
		this.ip = ip;
		this.port = port;
		this.data = data;
	}

	/**
	 * Constructor with split up byte array made by toByteArray method.
	 * @param header The first HEADER_SIZE bytes of the message
	 * @param data The remaining bytes with the header removed
	 */
	public Message(byte[] header, byte[] data) throws UnknownHostException	{
		ByteBuffer bbIp = ByteBuffer.allocate(4);
		ByteBuffer bbPort = ByteBuffer.allocate(2);
		this.ip = InetAddress.getByAddress(bbIp.put(header,0,4).array());
		this.port = bbPort.put(header,4,2).getShort(0);
		this.data = new String(data);
	}

	/**
	 * Constructor with whole byte array made by toByteArray method.
	 * @param bytes A byte array representing a message made with toByteArray
	 */
	public Message(byte[] bytes) throws UnknownHostException{
		ByteBuffer bbIp = ByteBuffer.allocate(4);
		ByteBuffer bbPort = ByteBuffer.allocate(2);
		this.ip = InetAddress.getByAddress(bbIp.put(bytes,0,4).array());
		this.port = bbPort.put(bytes,4,2).getShort(0);
		byte[] data = new byte[bytes.length-HEADER_SIZE];
		for(int i = HEADER_SIZE; i < bytes.length;i++){
			data[i-HEADER_SIZE] = bytes[i];
		}
		this.data = new String(data);
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
	 * @return the payload attached to this message
	 */
	public String getData(){return data;}
	/**
	 * @return the payload size attached to this message
	 */
	public int getSize(){return data.length();}

	/**
	 * @param ip the source IP of this message
	 */
	public void setIp(InetAddress ip){
		this.ip = ip;
	}

	/**
	 * @param port the source port of this message
	 */
	public void setPort(short port){
		this.port = port;
	}

	/**
	 * @param data the payload of this message
	 */
	public void setData(String data){
		this.data = data;
	}

	/**
	 * @return the byte array representing this message in the prescribed format
	 */
	public byte[] toByteArray(){
		ByteBuffer arr = ByteBuffer.allocate(HEADER_SIZE+data.length());
		arr.put(ip.getAddress());
		arr.putShort(port);
		arr.putInt(data.length());
		arr.put(data.getBytes());
		return arr.array();
	}
}