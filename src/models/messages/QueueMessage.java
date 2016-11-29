package models.messages;

import java.net.InetSocketAddress;
import java.util.UUID;

import models.MessageType;

public class QueueMessage extends Message {
	private Message queuedMessage;
	private UUID messageId;
	
	protected QueueMessage(InetSocketAddress senderSocketAddress, Message message, UUID messageId) {
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
	protected String getData() {
		return MessageType.QUEUE.getProtocolCode();
	}
}
