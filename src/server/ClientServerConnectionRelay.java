package server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import helpers.MessageReadHelper;
import models.messages.Message;
import models.User;

public class ClientServerConnectionRelay implements ClientConnectionListener {
	
	private List<Socket> serverSockets;
	
	public ClientServerConnectionRelay(List<Socket> serverSockets){
		this.serverSockets = serverSockets;
	}

	public String sendMessage(Socket socket, Message outgoing){
		String response = "";
		synchronized(socket){
			try{
				InputStream serverInputStream = socket.getInputStream();
				OutputStream serverOutputStream = socket.getOutputStream();
				
				serverOutputStream.write(outgoing.toByteArray());
				serverOutputStream.flush();
				
				Message incoming = MessageReadHelper.readNextMessage(socket.getInputStream());
				response = incoming.getData().substring(4);
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		return response;
	}
	
	@Override
	public String hostRequest(User user, String roomName) {
		String result = "";
		for(Socket socket : serverSockets){
			Message outgoing = new Message(user.getIpAddress(), user.getPort(), String.format("HST %s", user.getUsername()));
			result = sendMessage(socket, outgoing);
		}
		return result;
	}

	@Override
	public String roomRequest(User user) {
		String result ="";
		for(Socket socket : serverSockets){
			Message outgoing = new Message(user.getIpAddress(), user.getPort(), "ROM");
			result = sendMessage(socket, outgoing);
		}
		//TODO: change to list of chatroom
		return result;
	}

	@Override
	public String updateHost() {
		// TODO: eventually make this update the host
		return "";
	}

}
