package models.messages;

import models.MessageType;
import models.User;

import java.net.InetSocketAddress;

public class RequestRoomsMessage extends Message {

    public RequestRoomsMessage(final User sender) {
        super(sender.getSocketAddress());
    }

    public RequestRoomsMessage(final InetSocketAddress senderSocketAddress) {
        super(senderSocketAddress);
    }

    @Override
    public String getData() {
        return MessageType.REQUEST_ROOMS.getProtocolCode();
    }
}
