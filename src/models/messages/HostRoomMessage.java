package models.messages;

import models.MessageType;

import java.net.InetSocketAddress;

public class HostRoomMessage extends Message {
    private byte[] encryptedHostInformation;

    public HostRoomMessage(final InetSocketAddress senderSocketAddress, byte[] encryptedHostInformation) {
        super(senderSocketAddress);
        this.encryptedHostInformation = encryptedHostInformation;
    }
    
    public byte[] getRoomData() {
    	return encryptedHostInformation;
    }
    
    @Override
    protected String getData() {
        return String.format("%s %s", MessageType.HOST_ROOM.getProtocolCode(), encryptedHostInformation);
    }
}
