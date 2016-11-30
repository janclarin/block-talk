package models;
import models.messages.ChatMessage;
/**
 * This class is for holding a queued chat message to be released when the timestamp is
 * found to be low enough. It can be compared to others with compareTo to see which is earlier.
 */
public class SenderMessageTuple implements Comparable<SenderMessageTuple> {
	public final User sender;
	public final ChatMessage message;
	public SenderMessageTuple(User sender, ChatMessage message) {
		this.sender = sender;
		this.message = message;
	}

	/**
 	* Compares two messages to see which came earlier
 	* @return int Comparison of this tuple's timestamp with the other
 	*/
 	@Override
	public int compareTo(SenderMessageTuple otherTuple) {
		return Integer.valueOf(this.message.getTimestamp()).compareTo(Integer.valueOf(otherTuple.message.getTimestamp()));
	}
}