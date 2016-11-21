package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import models.User;

public class ClientServerConnectionRelay implements ClientConnectionListener {
	
	private List<Socket> serverSockets;
	
	public ClientServerConnectionRelay(List<Socket> serverSockets){
		this.serverSockets = serverSockets;
	}

	public String sendMessage(Socket socket, String outgoing){
		String response = "";
		//synchronized(socket){
			try{
				BufferedReader serverInputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter serverOutputStream = new PrintWriter(socket.getOutputStream());
				
				serverOutputStream.println(outgoing);
				serverOutputStream.flush();
				
				response = serverInputStream.readLine();
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		//}
		return response;
	}
	
	@Override
	public boolean hostRequest(User user, String roomName) {
		boolean result = false;
		for(Socket socket : serverSockets){
			String outgoing = String.format("HST %s %s", user.getUsername(), roomName);
			String response = sendMessage(socket, outgoing);
			result = Boolean.parseBoolean(response);
		}
		return result;
	}

	@Override
	public String roomRequest() {
		String result = " ";
		for(Socket socket : serverSockets){
			String outgoing = String.format("ROM");
			result = sendMessage(socket, outgoing);
		}
		return result;
	}

	@Override
	public boolean updateHost() {
		// TODO Auto-generated method stub
		return false;
	}

}
