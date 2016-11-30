package models.messages;

import models.MessageType;
import models.User;

import java.net.InetSocketAddress;

public class RequestRoomListMessage extends Message {

    public RequestRoomListMessage(final User sender) {
        super(sender.getSocketAddress());
    }

    public RequestRoomListMessage(final InetSocketAddress senderSocketAddress) {
        super(senderSocketAddress);
    }

    @Override
    public String getData() {
        return MessageType.REQUEST_ROOM_LIST.getProtocolCode();
    }
}
