package models.messages;

import models.MessageType;
import models.User;

/**
 * Leader message to claim new hosting rights.
 * HLO <senderUsername> <sourceAddress>
 */
public class LeaderMessage extends Message {

    private final User sender;

    public LeaderMessage(final User sender) {
        super(sender.getSocketAddress());
        this.sender = sender;
    }

    public User getSender() {
        return sender;
    }

    @Override
    protected String getData() {
        return String.format("%s %s %s %d", MessageType.LEADER.getProtocolCode(),
                sender.getUsername(), this.senderSocketAddress.getHostString(), this.senderSocketAddress.getPort());
    }
}
