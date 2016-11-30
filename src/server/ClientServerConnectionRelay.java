package server;

import helpers.MessageReadHelper;
import models.messages.Message;
import models.messages.ProcessMessage;
import models.messages.QueueMessage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ClientServerConnectionRelay implements ClientConnectionListener {

    private List<Socket> serverSockets;

    public ClientServerConnectionRelay(List<Socket> serverSockets) {
        this.serverSockets = serverSockets;
    }

    public Message sendMessage(Socket socket, Message outgoing) throws IOException {
        synchronized (socket) {
            OutputStream serverOutputStream = socket.getOutputStream();
            serverOutputStream.write(outgoing.toByteArray());
            serverOutputStream.flush();
            return MessageReadHelper.readNextMessage(socket.getInputStream());
        }
    }
    
    public Message sendToServers(Message message, UUID messageId){
    	List<Message> replies = new ArrayList<Message>();
    	for(Socket serverSocket : serverSockets){
    		Message reply = null;
    		do{
        		try{
            		reply = sendMessage(serverSocket, message);
        		} catch (Exception ex){
        			reply = null;
        			ex.printStackTrace();
        		}
    		} while (!checkMessageId(reply, messageId));
    		replies.add(reply);	
    	}
    	return replies.iterator().next();
    }

    @Override
    public Message messageReceived(Message message) {
        Message responseMessage = null;
        UUID queueId =  UUID.randomUUID();
        responseMessage = sendToServers(new QueueMessage(message.getSenderSocketAddress(), message, queueId), queueId);
        responseMessage = sendToServers(new ProcessMessage(message.getSenderSocketAddress(), queueId), queueId);
        return responseMessage;
    }
    
    public boolean checkMessageId(Message message, UUID messageId) {
    	if(message == null){
    		return false;
    	} else if(message instanceof ProcessMessage
				&& !((ProcessMessage)message).getMessageId().equals(messageId)){
			return false;
		} else{
			return true;
		}
    }
    
}
