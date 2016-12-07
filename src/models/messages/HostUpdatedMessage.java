package models.messages;

import java.net.InetSocketAddress;

import models.MessageType;

public class HostUpdatedMessage extends Message {
	private String token;
	private byte[] newEncryptedHost;
	
	protected HostUpdatedMessage(InetSocketAddress senderSocketAddress, String token, byte[] encryptedHost) {
		super(senderSocketAddress);
		this.token = token;
		this.newEncryptedHost = encryptedHost;
	}
	
	public String getToken(){
		return token;
	}
	
	public byte[] getEncryptedHost(){
		return newEncryptedHost;
	}
	
    @Override
    protected String getData() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MessageType.HOST_UPDATED.getProtocolCode());
        stringBuilder.append(" ");
        stringBuilder.append(token);
        stringBuilder.append("\n");
        stringBuilder.append(newEncryptedHost);
        return stringBuilder.toString();
    }
}
