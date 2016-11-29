package server;

import helpers.MessageReadHelper;
import models.User;
import models.messages.AckMessage;
import models.messages.HostRoomMessage;
import models.messages.Message;
import models.messages.ProcessMessage;
import models.messages.QueueMessage;
import models.messages.RequestRoomListMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
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
        		}
        		catch (Exception ex){
        			ex.printStackTrace();
        		}
    		}
    		while (checkMessageId(reply, messageId));
    		replies.add(reply);	
    	}
    	replies.removeAll(Collections.singleton(null));
    	return replies.iterator().next();
    }

    @Override
    public Message messageReceived(Message message) {
        Message responseMessage = null;
        List<Message> replies = null;
        UUID queueId =  UUID.randomUUID();
        responseMessage = sendToServers(new QueueMessage(null, message, queueId), queueId);
        responseMessage = sendToServers(new ProcessMessage(null, queueId), queueId);
        return responseMessage;
    }
    
    public boolean checkMessageId(Message message, UUID messageId) {
		if(message instanceof AckMessage
				&& !((AckMessage) message).getInformation().substring(7).equals(messageId.toString())){
			return false;
		}
		else{
			return true;
		}
    }
    
}
