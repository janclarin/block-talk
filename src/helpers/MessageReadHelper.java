package helpers;
import models.messages.Message;
import java.io.InputStream;
import java.io.IOException;

public class MessageReadHelper{

	public static Message readNextMessage(InputStream is) throws IOException{
		while(is.available()<Message.HEADER_SIZE){}
        byte[] header = new byte[Message.HEADER_SIZE];
        is.read(header);
        Message message = new Message(header, new byte[0]);
        byte[] data = new byte[message.parseSize(header)];
        while(is.available()<data.length){}
        is.read(data);
        message.setData(new String(data));
        return message;
	}
}