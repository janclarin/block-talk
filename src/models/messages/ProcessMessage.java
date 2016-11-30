package models.messages;

import models.MessageType;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * Message class to be sent when data is ready to be processed. Contains UUID of data to be processed. 
 */
public class ProcessMessage extends Message {
	private UUID messageId;

    public ProcessMessage(final InetSocketAddress senderSocketAddress, UUID messageId) {
        super(senderSocketAddress);
        this.messageId = messageId;
    }
    
    public UUID getMessageId(){
    	return messageId;
    }
    
    public boolean hasMessageId(UUID messageId) {
        return this.messageId.equals(messageId);
    }

    @Override
    protected String getData() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MessageType.PROCESS.getProtocolCode());
        stringBuilder.append("\n");
        stringBuilder.append(messageId.toString());
        return stringBuilder.toString();
    }
}
