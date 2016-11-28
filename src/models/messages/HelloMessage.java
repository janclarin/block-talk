package models.messages;

import models.MessageType;
import models.User;

/**
 * Hello message to pass the senderUsername and the sourcePort to connect to for communication.
 * HLO <senderUsername> <sourcePort>
 */
public class HelloMessage extends Message {

    private final User sender;

    public HelloMessage(final User sender) {
        super(sender.getSocketAddress());
        this.sender = sender;
    }

    public User getSender() {
        return sender;
    }

    @Override
    protected String getData() {
        return String.format("%s %s %s %d", MessageType.HELLO.getProtocolCode(),
                sender.getUsername(), this.senderSocketAddress.getHostString(), this.senderSocketAddress.getPort());
    }
}
