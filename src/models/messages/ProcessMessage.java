package models.messages;

import models.MessageType;

import java.net.InetSocketAddress;
import java.util.UUID;

public class ProcessMessage extends Message {
	private UUID messageId;

    public ProcessMessage(final InetSocketAddress senderSocketAddress, UUID messageId) {
        super(senderSocketAddress);
        this.messageId = messageId;
    }
    
    public UUID getMessageId(){
    	return messageId;
    }

    @Override
    protected String getData() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MessageType.PROCESSDATA.getProtocolCode());
        stringBuilder.append("\n");
        stringBuilder.append(messageId.toString());
        return stringBuilder.toString();
    }
}
