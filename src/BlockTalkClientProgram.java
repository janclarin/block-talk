import chatroom.Client;
import chatroom.ClientListener;
import models.User;

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
        if (args.length != 2 && args.length != 4) {
            throw new IllegalArgumentException();
        }

        String clientUsername = args[0]+":"+args;
        int clientPort = Integer.parseInt(args[1]);
        BlockTalkClientProgram program = new BlockTalkClientProgram();
        Client client = new Client(clientUsername, clientPort, program);
        new Thread(client).start();

        // Determine if we should broadcast a message to a specified user immediately.
        if (args.length == 4) {
            String otherHost = args[2];
            int otherHostPort = Integer.parseInt(args[3]);
            User otherUser = new User("OtherUser:" + otherHostPort, InetAddress.getByName(otherHost), otherHostPort);
            String message = String.format("Sent from (%s) to (%s)", clientPort, otherUser);
            client.sendMessage(message, otherUser);

            Scanner scan = new Scanner(System.in);
            while(!message.equals("/q"))
            {
                message = scan.nextLine();
                client.sendMessageToAll(message);
            }
        }
    }
}
