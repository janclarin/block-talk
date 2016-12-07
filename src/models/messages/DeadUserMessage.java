package models.messages;

import models.MessageType;
import models.User;

import java.net.InetSocketAddress;

/**
 * Dead user info message:
 * DED <username> <sourceIpAddress> <sourcePort>
 */
public class DeadUserMessage extends Message {

    private final User deadUser;

    public DeadUserMessage(final User sender, final User deadUser) {
        super(sender.getSocketAddress());
        this.deadUser = deadUser;
    }

    public DeadUserMessage(final InetSocketAddress senderSocketAddress, final User deadUser) {
        super(senderSocketAddress);
        this.deadUser = deadUser;
    }

    /**
     * Returns the user whose information was sent.
     * @return Sent user information.
     */
    public User getDeadUser() {
        return deadUser;
    }

    /**
     * @return the payload attached to this message
     */
    @Override
    public String getData() {
        return String.format("%s %s %s %d", MessageType.DEAD_USER.getProtocolCode(), deadUser.getUsername(),
                deadUser.getIpAddress().getHostAddress(), deadUser.getPort());
    }
}
