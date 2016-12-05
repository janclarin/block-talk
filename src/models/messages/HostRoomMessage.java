package models.messages;

import models.MessageType;

import java.net.InetSocketAddress;
import java.util.Base64;

public class HostRoomMessage extends Message {
    private byte[] encryptedHostInformation;

    public HostRoomMessage(final InetSocketAddress senderSocketAddress, byte[] encryptedHostInformation) {
        super(senderSocketAddress);
        this.encryptedHostInformation = encryptedHostInformation;
    }

    public HostRoomMessage(final InetSocketAddress senderSocketAddress, String encryptedHostInformation) {
        super(senderSocketAddress);
        this.encryptedHostInformation = parseString(encryptedHostInformation);
    }
    
    public byte[] getRoomData() {
    	return encryptedHostInformation;
    }

    private byte[] parseString(String messageContent) {
        messageContent = messageContent.replace(MessageType.HOST_ROOM.getProtocolCode(), "").trim();
        return Base64.getDecoder().decode(messageContent);
    }
    
    @Override
    protected String getData() {
        return String.format("%s %s", MessageType.HOST_ROOM.getProtocolCode(), Base64.getEncoder().encode(encryptedHostInformation));
    }
}
