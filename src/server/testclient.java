package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
	public static void main(String[] args) throws InterruptedException{
		try {
			Socket sock = new Socket("localhost", 9999);
			
			BufferedReader fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			PrintWriter toServer = new PrintWriter (sock.getOutputStream());
			
			toServer.println("HLO swag 9999");
			toServer.flush();
			String response1 = fromServer.readLine();
			
			toServer.println("HST teatime");
			toServer.flush();
			String response = fromServer.readLine();
			
			toServer.println("ROM");
			toServer.flush();
			response = fromServer.readLine();
			
			System.out.println(response);
			sock.close();
		} 
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
}
