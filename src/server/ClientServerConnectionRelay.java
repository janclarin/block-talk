package server;

import helpers.MessageReadHelper;
import models.messages.ByeMessage;
import models.messages.Message;
import models.messages.ProcessMessage;
import models.messages.QueueMessage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
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
    
    /**
     * Send messages to server sockets until a valid reply is received. Returns first message in the list.
     * 
     * @param message
     * @param messageId
     * @return
     */
    public Message sendMessageToServerSockets(Message message, UUID messageId){
    	List<Message> replies = new ArrayList<Message>();
		Message reply = null;
    	for(Socket serverSocket : serverSockets){
    		do{
        		try{
					reply = sendMessage(serverSocket, message);
					if(reply instanceof ProcessMessage){
						reply = ((ProcessMessage)reply).hasMessageId(messageId) ? reply : null;
					}
        		} catch (IOException ex){
        			reply = new ByeMessage((InetSocketAddress) serverSocket.getLocalSocketAddress());
        			//ex.printStackTrace();
        		}
    		} while (reply == null);
    		if(!(reply instanceof ByeMessage)){
        		replies.add(reply);	
    		}
    	}
    	return replies.iterator().next();
    }

    @Override
    public Message messageReceived(Message message) {
        Message responseMessage = null;
        UUID queueId =  UUID.randomUUID();
        responseMessage = sendMessageToServerSockets(new QueueMessage(message.getSenderSocketAddress(), message, queueId), queueId);
        responseMessage = sendMessageToServerSockets(new ProcessMessage(message.getSenderSocketAddress(), queueId), queueId);
        return responseMessage;
    }  
    
}
