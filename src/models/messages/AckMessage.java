package models.messages;

import models.MessageType;

import java.net.InetSocketAddress;

public class AckMessage extends Message {

    private final String information;

    public AckMessage(final InetSocketAddress senderSocketAddress) {
        super(senderSocketAddress);
        this.information = "";
    }

    public AckMessage(final InetSocketAddress senderSocketAddress, final String information) {
        super(senderSocketAddress);
        this.information = information;
    }

    public String getInformation() {
        return information;
    }

    @Override
    protected String getData() {
        return String.format("%s %s", MessageType.ACKNOWLEDGEMENT.getProtocolCode(), information);
    }
}
