package models.messages;

import models.ChatRoom;
import models.MessageType;

import java.net.InetSocketAddress;
import java.util.List;

public class RoomListMessage extends Message {

    private final List<ChatRoom> chatRooms;

    public RoomListMessage(final InetSocketAddress senderSocketAddress, final List<ChatRoom> chatRooms) {
        super(senderSocketAddress);
        this.chatRooms = chatRooms;
    }

    public List<ChatRoom> getChatRooms() {
        return chatRooms;
    }

    @Override
    protected String getData() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MessageType.ROOM_LIST.getProtocolCode());
        stringBuilder.append(" ");
        for (ChatRoom chatRoom : chatRooms) {
            stringBuilder.append(chatRoom.getName());
            stringBuilder.append(" ");
            stringBuilder.append(chatRoom.getHostIpAddress().getHostAddress());
            stringBuilder.append(" ");
            stringBuilder.append(chatRoom.getHostPort());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
