package models.messages;

import java.net.InetSocketAddress;
import java.util.Base64;

import models.MessageType;

public class HostUpdatedMessage extends Message {
	private String token;
	private byte[] newEncryptedHost;
	
	public HostUpdatedMessage(InetSocketAddress senderSocketAddress, String token, byte[] encryptedHost) {
		super(senderSocketAddress);
		this.token = token;
		this.newEncryptedHost = encryptedHost;
	}

	public HostUpdatedMessage(InetSocketAddress senderSocketAddress, String content) {
		super(senderSocketAddress);
		String[] splitContent = content.split(" ");
		this.token = splitContent[0];
		this.newEncryptedHost = Base64.getDecoder().decode(splitContent[1]);
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
        stringBuilder.append(" ");
        stringBuilder.append(new String(Base64.getEncoder().encode(newEncryptedHost)));
        return stringBuilder.toString();
    }
}
