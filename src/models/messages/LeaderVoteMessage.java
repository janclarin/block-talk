package models.messages;

import models.MessageType;
import models.User;

/**
 * LeaderVote message to pass the senderUsername and the sourcePort to send a vote in receivers favour.
 * VOT <senderUsername> <sourceAddress>
 */
public class LeaderVoteMessage extends Message {

    private final User sender;

    public LeaderVoteMessage(final User sender) {
        super(sender.getSocketAddress());
        this.sender = sender;
    }

    public User getSender() {
        return sender;
    }

    @Override
    protected String getData() {
        return String.format("%s %s %s %d", MessageType.LEADER_VOTE.getProtocolCode(),
                sender.getUsername(), this.senderSocketAddress.getHostString(), this.senderSocketAddress.getPort());
    }
}
