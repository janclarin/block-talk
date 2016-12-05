package models.messages;

import models.MessageType;

import java.net.InetSocketAddress;

public class HostRoomMessage extends Message {

    private final String roomName;
    //TODO: set roomBytes to encrypted data through constructor
    private byte[] encryptedHostInformation;

    public HostRoomMessage(final InetSocketAddress senderSocketAddress, final String roomName) {
        super(senderSocketAddress);
        this.roomName = roomName;
    }
    

    public String getRoomName() {
        return roomName;
    }
    
    public byte[] getRoomData() {
    	return encryptedHostInformation;
    }
    
    @Override
    protected String getData() {
        return String.format("%s %s", MessageType.HOST_ROOM.getProtocolCode(), roomName);
    }
}
