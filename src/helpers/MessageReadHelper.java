package helpers;
import exceptions.MessageTypeNotSupportedException;
import models.ChatRoom;
import models.MessageType;
import models.User;
import models.messages.*;

import java.io.InputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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
    private static Message createMessage(byte[] header, byte[] data) throws UnknownHostException, MessageTypeNotSupportedException {
	    InetAddress headerIpAddress = getHeaderIpAddress(header);
	    int headerPort = getHeaderPort(header);
        InetSocketAddress senderSocketAddress = new InetSocketAddress(headerIpAddress, headerPort);

        MessageType messageType = getDataMessageType(data);
        String messageContent = getDataMessageContent(data);

        switch (messageType) {
            case ACKNOWLEDGEMENT:
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
                // This assumes that the room name is the only thing in the message body.
                return new HostRoomMessage(senderSocketAddress, messageContent);
            case REQUEST_ROOM_LIST:
                return new RequestRoomListMessage(senderSocketAddress);
            case ROOM_LIST:
                List<ChatRoom> chatRooms = getMessageContentChatRooms(messageContent);
                return new RoomListMessage(senderSocketAddress, chatRooms);
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
                throw new MessageTypeNotSupportedException();
        }
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

    /**
     * Creates a list of chat rooms from message content.
     * Expects a format of:
     * <chatroomName> <hostIpAddress> <hostPort>\n<chatroomName> <hostIpAddress> <hostPort>\n ...
     *
     * @param messageContent Message content string.
     * @return List of ChatRooms parsed from message content.
     */
    private static List<ChatRoom> getMessageContentChatRooms(String messageContent) {
        List<ChatRoom> chatRooms = new ArrayList<>();
        String[] messageContentSplitByNewLines = messageContent.split("\n");
        for (String chatRoomString : messageContentSplitByNewLines) {
            String[] chatRoomStringSplit = chatRoomString.split(" ");
            String chatRoomName = chatRoomStringSplit[0];
            String chatRoomHostIpAddress = chatRoomStringSplit[1];
            int chatRoomHostPort = Integer.parseInt(chatRoomStringSplit[2]);
            InetSocketAddress chatRoomHostSocketAddress = new InetSocketAddress(chatRoomHostIpAddress, chatRoomHostPort);
            chatRooms.add(new ChatRoom(chatRoomName, chatRoomHostSocketAddress));
        }
        return chatRooms;
    }

    /**
     * Split timestamp and chat message content
     * Expecting the format:
     * <timestamp> <message>
     *
     * @param messageContent Message content string.
     * @return String[] Containing {timestamp, content}
     */
    private static String[] splitChatMessage(String messageContent){
        //Split at first space (should be after the timestamp)
        String[] splitContent = messageContent.split(" ",2);
        if (Integer.parseInt(splitContent[0]) < 0) {
           throw new IllegalArgumentException("Argument must be of the form: <timestamp> <content>.");
        }
        return splitContent;
    }
}