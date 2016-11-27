package helpers;
import models.MessageType;
import models.User;
import models.messages.*;

import java.io.InputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class MessageReadHelper{

    /**
     * Reads the next Message in the input stream.
     * @param inputStream The input stream to read from.
     * @return The read Message.
     * @throws IOException Thrown when there is an issue reading a message.
     */
	public static Message readNextMessage(InputStream inputStream) throws IOException{
        byte[] header = readNextBytes(inputStream, Message.BYTE_HEADER_SIZE);
        int dataSize = parseHeaderDataSize(header);
        byte[] data = readNextBytes(inputStream, dataSize);
        return createMessage(header, data);
	}

    /**
     * Reads the next bytesToRead bytes from the input stream.
     * @param inputStream The input stream to read from.
     * @param bytesToRead The number of bytes to read from the input stream.
     * @return The read bytes.
     * @throws IOException Thrown when there is an issue reading the number of bytes.
     */
	private static byte[] readNextBytes(InputStream inputStream, int bytesToRead) throws IOException {
	    while (inputStream.available() < bytesToRead);
        byte[] bytes = new byte[bytesToRead];
        inputStream.read(bytes);
        return bytes;
    }

    /**
     * Parses the data size (in bytes) from the header.
     * @param header Message header as bytes.
     * @return Size (in bytes) of the Message's data.
     */
	private static int parseHeaderDataSize(byte[] header) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        return buffer.put(header, 8, 4).getInt(0);
    }

    private static Message createMessage(byte[] header, byte[] data) throws UnknownHostException, IllegalArgumentException {
        InetSocketAddress senderSocketAddress = new InetSocketAddress(getHeaderIpAddress(header), getHeaderPort(header));

        MessageType messageType = getDataMessageType(data);
        String messageContent = getDataMessageContent(data);

        switch (messageType) {
            case ACKNOWLEDGEMENT:
                return new AckMessage(senderSocketAddress);
            case BYE:
                return new ByeMessage(senderSocketAddress);
            case MESSAGE:
                return new ChatMessage(senderSocketAddress, messageContent);
            case HELLO:
                User sender = getMessageContentUser(messageContent);
                return new HelloMessage(sender);
            case USER:
                User contentUser = getMessageContentUser(messageContent);
                return new UserInfoMessage(senderSocketAddress, contentUser);
            case YOU:
                User yourContentUser = getMessageContentUser(messageContent);
                return new YourInfoMessage(senderSocketAddress, yourContentUser);
            // TODO: case DISCONNECTED:
            // TODO: case LEADER:
            // TODO: case NEGATIVE_ACKNOWLEDGEMENT:
            // TODO: case ORDER:
            default:
                throw new IllegalArgumentException(String.format("Message type %s is not supported yet.", messageType.toString()));
        }
    }

    private static String getHeaderIpAddress(byte[] header) {
        ByteBuffer inetAddressByteBuffer = ByteBuffer.allocate(4);
        inetAddressByteBuffer.put(header, 0, 4);
        return new String(inetAddressByteBuffer.array());
    }

    private static int getHeaderPort(byte[] header) {
        ByteBuffer portByteBuffer = ByteBuffer.allocate(4);
        return portByteBuffer.put(header, 4, 4).getInt(0);
    }

    private static MessageType getDataMessageType(byte[] data) {
        String dataString = new String(data);
        for (MessageType messageType : MessageType.values()) {
            if (dataString.startsWith(messageType.getProtocolCode())) {
                return messageType;
            }
        }
        return null;
    }

    /**
     * Ignores the first 4 bytes of the data which is reserved as the message type.
     * @param data Data bytes.
     * @return Message content.
     */
    private static String getDataMessageContent(byte[] data) {
        return new String(data).substring(4);
    }

    /**
     * Creates a user from message content.
     * Expecting the format:
     * <username> <ipAddress> <port>
     * @param messageContent
     * @return
     */
    private static User getMessageContentUser(String messageContent) throws UnknownHostException {
        String[] messageContentSplit = messageContent.split(" ");
        if (messageContentSplit.length != 3) {
            throw new IllegalArgumentException("Argument must be of the form: <username> <ipAddress> <port>.");
        }

        String username = messageContentSplit[0];
        String ipAddress = messageContentSplit[1];
        int port = Integer.parseInt(messageContentSplit[2]);
        return new User(username, new InetSocketAddress(ipAddress, port));
    }
}