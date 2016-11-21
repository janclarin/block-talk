package server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import helpers.MessageReadHelper;
import models.Message;

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
			
			InputStream fromServer = sock.getInputStream();
			OutputStream toServer = sock.getOutputStream();
			
			Message msg;
			
			toServer.write((new Message(InetAddress.getLocalHost(), 9999, "HLO client1 9999").toByteArray()));
			toServer.flush();
			String response1 = MessageReadHelper.readNextMessage(fromServer).getData();
			
			toServer.write((new Message(InetAddress.getLocalHost(), 9999, "HST teatime").toByteArray()));
			toServer.flush();
			String response2 = MessageReadHelper.readNextMessage(fromServer).getData();
			
			toServer.write((new Message(InetAddress.getLocalHost(), 9999, "ROM").toByteArray()));
			toServer.flush();
			String response = MessageReadHelper.readNextMessage(fromServer).getData();
			
			System.out.println(response);
			sock.close();
		} 
		catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
}
