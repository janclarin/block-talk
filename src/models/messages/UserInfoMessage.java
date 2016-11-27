package models.messages;

import models.MessageType;
import models.User;

import java.net.InetSocketAddress;

/**
 * User info message:
 * USR <username> <sourceIpAddress>:<sourcePort>
 */
public class UserInfoMessage extends Message {

    private final User user;

    public UserInfoMessage(final User sender, final User user) {
        super(sender.getSocketAddress());
        this.user = user;
    }

    public UserInfoMessage(final InetSocketAddress senderSocketAddress, final User user) {
        super(senderSocketAddress);
        this.user = user;
    }

    /**
     * Returns the user whose information was sent.
     * @return Sent user information.
     */
    public User getUser() {
        return user;
    }

    /**
     * @return the payload attached to this message
     */
    @Override
    public String getData() {
        return String.format("%s %s %s %d", MessageType.USER.getProtocolCode(), user.getUsername(),
                user.getIpAddress().getHostAddress(), user.getPort());
    }
}
