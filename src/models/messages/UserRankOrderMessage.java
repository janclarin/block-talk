package models.messages;

import models.MessageType;
import models.User;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Used for passing user ranking order for leader election.
 * ORD <username1> <userIpAddress1> <userPort1>\n<username2> <userIpAddress2> <userPort2>\n...
 */
public class UserRankOrderMessage extends Message {

    private final List<User> userRankOrderList;

    public UserRankOrderMessage(final InetSocketAddress senderSocketAddress, final List<User> userRankOrderList) {
        super(senderSocketAddress);
        this.userRankOrderList = userRankOrderList;
    }

    public List<User> getUserRankOrderList() {
        return userRankOrderList;
    }

    /**
     * Formats data as follows:
     * ORD <username1> <userIpAddress1> <userPort1>\n<username2> <userIpAddress2> <userPort2>\n...
     * @return String formatted as described above.
     */
    @Override
    protected String getData() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MessageType.USER_RANK_ORDER.getProtocolCode());
        stringBuilder.append(" ");

        for (User user : userRankOrderList) {
            stringBuilder.append(user.getUsername());
            stringBuilder.append(" ");
            stringBuilder.append(user.getSocketAddress().getAddress().getHostAddress());
            stringBuilder.append(" ");
            stringBuilder.append(user.getSocketAddress().getPort());
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}
