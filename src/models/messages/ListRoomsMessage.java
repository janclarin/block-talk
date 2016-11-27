package models.messages;

import models.ChatRoom;
import models.MessageType;
import models.User;

import java.util.List;

public class ListRoomsMessage extends Message {

    private final List<ChatRoom> chatRooms;

    public ListRoomsMessage(final User sender, final List<ChatRoom> chatRooms) {
        super(sender.getSocketAddress());
        this.chatRooms = chatRooms;
    }

    public List<ChatRoom> getChatRooms() {
        return chatRooms;
    }

    @Override
    protected String getData() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MessageType.LIST_ROOMS.getProtocolCode());
        stringBuilder.append(" ");
        for (ChatRoom chatRoom : chatRooms) {
            stringBuilder.append(chatRoom.getName());
            stringBuilder.append(" @ ");
            stringBuilder.append(chatRoom.getHost().getHostAddress());
            stringBuilder.append(":");
            stringBuilder.append(chatRoom.getPort());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
