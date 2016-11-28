package models.messages;

import models.MessageType;

import java.net.InetSocketAddress;

public class ByeMessage extends Message {

    public ByeMessage(final InetSocketAddress senderSocketAddress) {
        super(senderSocketAddress);
    }

    @Override
    protected String getData() {
        return MessageType.BYE.getProtocolCode();
    }
}
