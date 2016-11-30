package models.messages;

import models.MessageType;

import java.net.InetSocketAddress;

public class ChatMessage extends Message {

    private final String message;

    private final int timestamp;

    public ChatMessage(final InetSocketAddress senderSocketAddress, final int timestamp, final String message) {
        super(senderSocketAddress);
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public int getTimestamp() {
        return timestamp;
    }

    @Override
    protected String getData() {
        return String.format("%s %d %s", MessageType.MESSAGE.getProtocolCode(),timestamp, message);
    }

    @Override
    public String toString() {
        return message;
    }
}
