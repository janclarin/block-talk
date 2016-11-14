import chatroom.Client;
import chatroom.ClientListener;
import models.User;

import java.util.List;
import java.util.Scanner;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Main program for running a Block Talk Client.
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class BlockTalkClientProgram implements ClientListener {
    @Override
    public void messageSent(User recipient, String message) {
        System.out.printf("Successfully sent message: %s\n", message);
    }

    @Override
    public void messageReceived(User sender, String message) {
        System.out.printf("%s: \"%s\"\n", sender.getUsername(), message);
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
        BlockTalkClientProgram program = new BlockTalkClientProgram();

        Scanner scan = new Scanner(System.in);
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
        client.sendMessage("HLO "+clientUsername+" "+clientPort,server);
        System.out.print("Enter message for server: ");
        client.sendMessage(scan.nextLine(),server);
        client.sendMessage("BYE",server);
        client.removeUserFromList(server);
        
        String message = "";
        while(!message.equals("/q")){
            message = scan.nextLine();
            //TODO: Move this logic to another class
            if(message.startsWith("/connect ")){
                User newUser = new User("NewUser", InetAddress.getByName(message.split(" ")[1]), Integer.parseInt(message.split(" ")[2]));
                client.sendMessage("HLO", newUser);
            }
            else if(message.startsWith("/list")){
                System.out.println("KNOWN USERS");
                List<User> users = client.getKnownUsersList();
                for(User u : users){System.out.println(u.toString());}
            }
            else if(message.startsWith("/")){}
            else{
                client.sendMessageToAll(message);
            }
        }
    }
}
