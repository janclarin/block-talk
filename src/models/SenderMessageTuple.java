package models;
import models.messages.ChatMessage;
public class SenderMessageTuple
{
	public final User sender;
	public final ChatMessage message;
	public SenderMessageTuple(User sender, ChatMessage message)
	{
		this.sender = sender;
		this.message = message;
	}
}