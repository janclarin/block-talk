package models.messages;

import models.MessageType;
import models.User;

import java.net.InetSocketAddress;

public class YourInfoMessage extends Message {

    private final User user;

    public YourInfoMessage(final User sender, final User user) {
        super(sender.getSocketAddress());
        this.user = user;
    }

    public YourInfoMessage(final InetSocketAddress senderSocketAddress, final User user) {
        super(senderSocketAddress);
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    protected String getData() {
        return String.format("%s %s %s %d", MessageType.YOU.getProtocolCode(), user.getUsername(),
                user.getIpAddress().getHostAddress(), user.getPort());
    }
}
