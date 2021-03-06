package server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;

import helpers.MessageReadHelper;
import models.messages.Message;
import models.User;
import models.messages.*;

/**
 * Example class: Interacting with the server
 * 
 * @author Clin
 * 
 * TODO: remove this entire class
 *
 */
public class testclient {
	@SuppressWarnings("unused")
	public static void main(String[] args) throws InterruptedException{
		try {
			Socket sock = new Socket("localhost", 9999);
			
			InputStream fromServer = sock.getInputStream();
			OutputStream toServer = sock.getOutputStream();
			
			Message msg;
			User user = new User("User1", new InetSocketAddress(InetAddress.getLocalHost(), 1000));
			
			HelloMessage hello = new HelloMessage(user);
			toServer.write(hello.toByteArray());
			toServer.flush();
			Message response1 = MessageReadHelper.readNextMessage(fromServer);	
			
			HostRoomMessage host = new HostRoomMessage(user.getSocketAddress(), "teatime".getBytes());
			toServer.write(host.toByteArray());
			toServer.flush();
			response1 = MessageReadHelper.readNextMessage(fromServer);
			
			RequestRoomListMessage rm = new RequestRoomListMessage(user.getSocketAddress());
			toServer.write(rm.toByteArray());
			toServer.flush();
			response1 = MessageReadHelper.readNextMessage(fromServer);

			sock.close();
		} 
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
}
