package models.messages;

import models.MessageType;

import java.net.InetSocketAddress;

public class ChatMessage extends Message {

    private final String message;

    public ChatMessage(final InetSocketAddress senderSocketAddress, final String message) {
        super(senderSocketAddress);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    protected String getData() {
        return String.format("%s %s", MessageType.MESSAGE.getProtocolCode(), message);
    }

    @Override
    public String toString() {
        return message;
    }
}
