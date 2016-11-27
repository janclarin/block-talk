package models.messages;

import models.MessageType;
import models.User;

import java.net.InetSocketAddress;

public class AckMessage extends Message {

    public AckMessage(InetSocketAddress senderSocketAddress) {
        super(senderSocketAddress);
    }

    @Override
    protected String getData() {
        return MessageType.ACKNOWLEDGEMENT.getProtocolCode();
    }
}
