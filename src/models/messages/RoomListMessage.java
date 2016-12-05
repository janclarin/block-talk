package models.messages;

import models.ChatRoom;
import models.MessageType;

import java.net.InetSocketAddress;
import java.util.List;

public class RoomListMessage extends Message {

    private final List<byte[]> roomBytesList;

    public RoomListMessage(final InetSocketAddress senderSocketAddress, final List<byte[]> roomBytesList) {
        super(senderSocketAddress);
        this.roomBytesList = roomBytesList;
    }

    public List<byte[]> getChatRooms() {
        return roomBytesList;
    }

    @Override
    protected String getData() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MessageType.ROOM_LIST.getProtocolCode());
        stringBuilder.append(" ");
        for (byte[] roomBytes : roomBytesList) {
            stringBuilder.append(roomBytes);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
