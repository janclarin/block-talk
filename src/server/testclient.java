package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Example class: Interacting with the server
 * 
 * @author Clin
 *
 */
public class testclient {
	@SuppressWarnings("unused")
	public static void main(String[] args){
		try {
			Socket sock = new Socket("localhost", 9999);
			DataInputStream fromServer = new DataInputStream(sock.getInputStream());
			DataOutputStream toServer = new DataOutputStream(sock.getOutputStream());
			
			toServer.writeUTF("HELLO swag");
			toServer.flush();
			String response = fromServer.readUTF();
			
			toServer.writeUTF("HOST teatime");
			toServer.flush();
			response = fromServer.readUTF();
			
			toServer.writeUTF("ROOM");
			toServer.flush();
			response = fromServer.readUTF();
			
			System.out.println(response);
			
			sock.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
