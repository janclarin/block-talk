package helpers;
import exceptions.MessageTypeNotSupportedException;
import models.MessageType;
import models.User;
import models.messages.*;
import encryption.*;

import java.io.InputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageReadHelper{

    /**
     * Reads the next Message in the input stream.
     *
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
     * Reads the next Encrypted Message in the input stream.
     *
     * @param inputStream The input stream to read from.
     * @return The read Message.
     * @throws IOException Thrown when there is an issue reading a message.
     */
    public static Message readNextEncryptedMessage(InputStream inputStream, EncryptionEngine encryptionEngine) throws IOException{
        byte[] header = readNextBytes(inputStream, Message.BYTE_HEADER_SIZE);
        int dataSize = parseHeaderDataSize(header);
        byte[] data = readNextBytes(inputStream, dataSize);
        return createMessageFromEncrypted(header, data, encryptionEngine);
    }

    /**
     * Reads the next bytesToRead bytes from the input stream.
     *
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
     *
     * @param header Message header as bytes.
     * @return Size (in bytes) of the Message's data.
     */
	private static int parseHeaderDataSize(byte[] header) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        return buffer.put(header, 8, 4).getInt(0);
    }

    /**
     * Creates a Message from bytes.
     *
     * @param header Message header as bytes.
     * @param data Message data as bytes.
     *
     * @return Message based on the header and data protocol.
     * @throws UnknownHostException
     * @throws IllegalArgumentException Thrown when the message
     */
    private static Message createMessage(byte[] header, byte[] data) throws UnknownHostException, MessageTypeNotSupportedException, IllegalArgumentException{
	    InetAddress headerIpAddress = getHeaderIpAddress(header);
	    int headerPort = getHeaderPort(header);
        InetSocketAddress senderSocketAddress = new InetSocketAddress(headerIpAddress, headerPort);

        MessageType messageType = getDataMessageType(data);
        String messageContent = getDataMessageContent(data);
        return createMessage(senderSocketAddress, messageType, messageContent);
    }
    
    /**
     * Creates a Message from bytes.
     *
     * @param header Message header as bytes.
     * @param data Message data as bytes.
     *
     * @return Message based on the header and data protocol.
     * @throws UnknownHostException
     */
    private static Message createMessage(InetSocketAddress senderSocketAddress, MessageType messageType, String messageContent) 
		throws UnknownHostException, MessageTypeNotSupportedException {
        switch (messageType) {
	        case ACKNOWLEDGEMENT:
	        	if(messageContent.length() > 1){
	        		return new AckMessage(senderSocketAddress, messageContent);
	        	}
	            return new AckMessage(senderSocketAddress);
	        case BYE:
	            return new ByeMessage(senderSocketAddress);
	        case MESSAGE:
                String[] content = splitChatMessage(messageContent);
                return new ChatMessage(senderSocketAddress, Integer.parseInt(content[0]),content[1]);
	        case HELLO:
	            User sender = getMessageContentUser(messageContent);
	            return new HelloMessage(sender);
	        case HOST_ROOM:
	            // This assumes that the encrypted info is the only thing in the message body.
	            return new HostRoomMessage(senderSocketAddress, messageContent);
	        case REQUEST_ROOM_LIST:
	            return new RequestRoomListMessage(senderSocketAddress);
	        case ROOM_LIST:
	            return new RoomListMessage(senderSocketAddress, messageContent);
	        case USER:
	            User contentUser = getMessageContentUser(messageContent);
	            return new UserInfoMessage(senderSocketAddress, contentUser);
	        case YOU:
	            User yourContentUser = getMessageContentUser(messageContent);
	            return new YourInfoMessage(senderSocketAddress, yourContentUser);
	        case PROCESS:
	        	UUID messageId = getMessageContentId(messageContent);
	        	return new ProcessMessage(senderSocketAddress, messageId);
	        case QUEUE:
	        	String idString = messageContent.split("\n")[0];
	        	String dataMessageString = messageContent.split("\n")[1];
	        	byte[] dataMessageBytes = dataMessageString.getBytes();
	        	UUID queueMessageId = getMessageContentId(idString);
	        	Message message = createMessage(senderSocketAddress, 
	        			getDataMessageType(dataMessageBytes), 
	        			getDataMessageContent(dataMessageBytes));
	        	return new QueueMessage(senderSocketAddress, message, queueMessageId);
	        case HOST_UPDATED:
	        	String token = messageContent.split(" ")[0];
	        	String encryptedHost = messageContent.split(" ")[1];
	        	return new HostUpdatedMessage(senderSocketAddress, token, encryptedHost.getBytes());
	        // TODO: case DISCONNECTED:
            case USER_RANK_ORDER:
                return new UserRankOrderMessage(senderSocketAddress, getMessageContentUserList(messageContent));
	        case DEAD_USER:
                User deadUser = getMessageContentUser(messageContent);
                return new DeadUserMessage(senderSocketAddress, deadUser);
	        // TODO: case LEADER:
	        // TODO: case NEGATIVE_ACKNOWLEDGEMENT:
	        default:
	            throw new MessageTypeNotSupportedException();
	    }
    }

    /**
     * Creates a Message from bytes which are encrypted with a matching EncryptionEngine.
     *
     * @param header Message header as bytes.
     * @param data Message data as bytes.
     * @param encryptionEngine The EncryptionEngine to decrypt with
     *
     * @return Message based on the header and data protocol.
     * @throws UnknownHostException
     * @throws IllegalArgumentException Thrown when the message
     * @throws MessageTypeNotSupportedException If the message does not match a known format
     */
    private static Message createMessageFromEncrypted(byte[] header, byte[] data, EncryptionEngine encryptionEngine) 
    throws UnknownHostException, MessageTypeNotSupportedException, IllegalArgumentException{
        InetAddress headerIpAddress = getHeaderIpAddress(header);
        int headerPort = getHeaderPort(header);
        InetSocketAddress senderSocketAddress = new InetSocketAddress(headerIpAddress, headerPort);

        EncryptedMessage encryptedMessage = new EncryptedMessage(senderSocketAddress, new String(data));
        data = encryptionEngine.decrypt(encryptedMessage.getCiphertext());
        return createMessage(header, data);
    }

    /**
     * Parses the IP address from the header.
     *
     * @param header Message header as bytes.
     * @return InetAddress from the header.
     * @throws UnknownHostException
     */
    private static InetAddress getHeaderIpAddress(byte[] header) throws UnknownHostException {
        ByteBuffer inetAddressByteBuffer = ByteBuffer.allocate(4);
        inetAddressByteBuffer.put(header, 0, 4);
        return InetAddress.getByAddress(inetAddressByteBuffer.array());
    }

    /**
     * Parses the port number from the header.
     *
     * @param header Message header as bytes.
     * @return Port number from the header.
     */
    private static int getHeaderPort(byte[] header) {
        ByteBuffer portByteBuffer = ByteBuffer.allocate(4);
        return portByteBuffer.put(header, 4, 4).getInt(0);
    }

    /**
     * Parses the message type from the data.
     *
     * @param data Message data as bytes.
     * @return MessageType based on data protocol.
     */
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
     *
     * @param data Data bytes.
     * @return Message content.
     */
    private static String getDataMessageContent(byte[] data) {
        String messageContentWithProtocol = new String(data);
        return messageContentWithProtocol.length() > 4 ? messageContentWithProtocol.substring(4) : "";
    }

    /**
     * Creates a user from message content.
     * Expecting the format:
     * <username> <ipAddress> <port>
     *
     * @param messageContent Message content string.
     * @return User parsed from message content.
     */
    private static User getMessageContentUser(String messageContent) throws UnknownHostException, IllegalArgumentException {
        String[] messageContentSplit = messageContent.split(" ");
        if (messageContentSplit.length != 3) {
            throw new IllegalArgumentException("Argument must be of the form: <username> <ipAddress> <port>.");
        }

        String username = messageContentSplit[0];
        String ipAddress = messageContentSplit[1];
        int port = Integer.parseInt(messageContentSplit[2]);
        return new User(username, new InetSocketAddress(ipAddress, port));
    }

    /**
     * Creates a list of users from message content.
     * Expecting the format:
     * <username1> <ipAddress1> <port1>\n<username2> <ipAddress2> <port2>\n...
     *
     * @param messageContent Message content string.
     * @return List<User> parsed from message content.</User>
     */
    private static List<User> getMessageContentUserList(String messageContent) throws UnknownHostException {
        List<User> users = new ArrayList<>();
        if (!messageContent.isEmpty()) {
            String[] messageContentSplitByNewLine = messageContent.split("\n");
            for (int i = 0; i < messageContentSplitByNewLine.length; i++) {
                users.add(getMessageContentUser(messageContentSplitByNewLine[i]));
            }
        }
        return users;
    }

    /**
     * Split timestamp and chat message content
     * Expecting the format:
     * <timestamp> <message>
     *
     * @param messageContent Message content string.
     * @return String[] Containing {timestamp, content}
     */
    private static String[] splitChatMessage(String messageContent) throws IllegalArgumentException{
        //Split at first space (should be after the timestamp)
        String[] splitContent = messageContent.split(" ",2);
        if (Integer.parseInt(splitContent[0]) < -1) {
           throw new IllegalArgumentException("Argument must be of the form: <timestamp> <content>.");
        }
        return splitContent;
    }
    
    /**
     * Creates a UUID from given message content
     * 
     * @param messageContent
     * @return
     */
    private static UUID getMessageContentId(String messageContent) {
    	return UUID.fromString(messageContent);
    }
}