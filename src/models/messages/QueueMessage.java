package models.messages;

import java.net.InetSocketAddress;
import java.util.UUID;
import models.MessageType;

/**
 * Message class to be sent to server when a message should be stored in queue to await processing.
 */
public class QueueMessage extends Message {
	private Message queuedMessage;
	private UUID messageId;
	
	public QueueMessage(InetSocketAddress senderSocketAddress, Message message, UUID messageId) {
		super(senderSocketAddress);
		queuedMessage = message;
		this.messageId = messageId;
	}

	public UUID getMessageId(){
		return messageId;
	}
	
	public Message getMessage(){
		return queuedMessage;
	}

	@Override
	public String getData() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MessageType.QUEUE.getProtocolCode());
        stringBuilder.append(" ");
        stringBuilder.append(messageId.toString());
        stringBuilder.append("\n");
        stringBuilder.append(queuedMessage.getData());
        return stringBuilder.toString();
	}
	
}
