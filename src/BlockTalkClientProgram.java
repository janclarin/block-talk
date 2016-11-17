import chatroom.Client;
import chatroom.ClientListener;
import models.User;

import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.InetAddress;
import java.net.UnknownHostException;

import models.Message;
import protocols.BlockTalkProtocol;

/**
 * Main program for running a Block Talk Client.
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class BlockTalkClientProgram implements ClientListener {
    @Override
    public void messageSent(User recipient, Message message) {
        System.out.printf("Successfully sent message: %s\n", message.getData());
    }

    @Override
    public void messageReceived(User sender, Message message) {
        System.out.println("MSGINFO: "+message.getIp().toString()+" "+message.getPort()+" "+message.getSize());
        if(message.getData().startsWith("MSG")){
            System.out.printf("%s: \"%s\"\n", sender.getUsername(), message.getData().substring(4));
        }
        else if(message.getData().startsWith("LST")){
            System.out.printf("%s: \"%s\"\n", sender.getUsername(), message.getData().substring(4));
        }
        else if(message.getData().startsWith("ACK")){
            System.out.printf("%s: \"%s\"\n", sender.getUsername(), message.getData().substring(4));
        }
        else if(message.getData().startsWith("HLO")){
            System.out.printf("%s: \"%s\"\n", sender.getUsername(), message.getData().substring(4));
        }
    }

    @Override
    public void userHasJoined(User newUser) {
        System.out.printf("New user has joined from %s\n", newUser);
    }

    /**
     * Main function to participate in chat rooms.
     * Must specify the following command line arguments:
     * - Host username
     * - Host port number
     * TODO: Remove the following:
     * Optional arguments (to connect to another client immediately (This is temporary until server is up)
     * - Other client IP address
     * - Other client port number
     * <p>
     * e.g. java BlockTalkClientProgram MyUsername 5001 [127.0.0.1 5002]
     *
     * @param args Command line arguments.
     * @throws UnknownHostException Invalid host.
     */
    public static void main(String[] args) throws UnknownHostException {
        if (args.length != 2) {
            throw new IllegalArgumentException();
        }
        int clientPort = Integer.parseInt(args[1]);
        InetAddress clientAddr = InetAddress.getByName(args[0]);
        BlockTalkClientProgram program = new BlockTalkClientProgram();

        Scanner scan = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
        System.out.print("Enter your username: ");
        String clientUsername = scan.nextLine();
        System.out.print("Enter server address: ");
        InetAddress serverAddr = InetAddress.getByName(scan.nextLine());
        System.out.print("Enter server port: ");
        int serverPort = Integer.parseInt(scan.nextLine());

        Client client = new Client(clientUsername, clientPort, program);
        new Thread(client).start();

        //Handshake with the server
        User server = new User("SERVER", serverAddr, serverPort);
        client.sendMessage(new Message(clientAddr,clientPort,"HLO "+clientUsername+" "+clientPort),server);
        System.out.print("Enter message for server: ");
        client.sendMessage(new Message(clientAddr,clientPort,scan.nextLine()),server);
        client.sendMessage(new Message(clientAddr,clientPort,"BYE"),server);
        client.removeUserFromList(server);
        
        String message = "";
        while(!message.equals("/q")){
            message = scan.nextLine();
            //TODO: Move this logic to another class
            if(message.startsWith("/connect ")){
                User newUser = new User("NewUser", InetAddress.getByName(message.split(" ")[1]), Integer.parseInt(message.split(" ")[2]));
                client.sendMessage(new Message(clientAddr,clientPort,"HLO "+clientUsername+" "+clientPort), newUser);
            }
            else if(message.startsWith("/list")){
                System.out.println("KNOWN USERS");
                List<User> users = client.getKnownUsersList();
                for(User u : users){System.out.println(u.toString());}
            }
            else if(message.startsWith("/HLO")){
                System.out.println("SEND HLO");
                client.sendMessageToAll(new Message(clientAddr,clientPort,"HLO "+clientUsername+" "+clientPort));
            }
            else if(message.startsWith("/")){}
            else{
                client.sendMessageToAll(new Message(clientAddr,clientPort,"MSG "+message));            
            }
        }
    }
}