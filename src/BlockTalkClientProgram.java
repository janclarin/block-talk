import chatroom.Client;
import chatroom.ClientListener;
import models.User;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.InetAddress;
import java.net.UnknownHostException;

import models.messages.*;

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
        //System.out.printf("Successfully sent message: %s\n", message.getData());
    }

    @Override
    public void messageReceived(User sender, Message message) {
        System.out.printf("%s: %s\n", sender != null ? sender.getUsername() : "NO NAME", message);
    }

    @Override
    public void userHasJoined(User newUser) {
        System.out.printf("New user has joined from %s\n", newUser);
    }

    /**
     * Main function to participate in chat rooms.
     *
     * @param args Command line arguments.
     * @throws UnknownHostException Invalid host.
     */
    public static void main(String[] args) throws UnknownHostException {
        BlockTalkClientProgram program = new BlockTalkClientProgram();
        Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

        // Prompt user for information about client and server.
        User clientUser = promptClientInfo(scanner);
        InetSocketAddress serverManagerSocketAddress = promptServerManagerAddress(scanner);

        // Start client.
        Client client = new Client(clientUser, program);
        new Thread(client).start();

        promptJoinOrHost(scanner, client, clientUser, serverManagerSocketAddress);

        // Listen for user input.
        String message = "";
        while(!message.equals("/q")){
            message = scanner.nextLine();
            //TODO: Move this logic to another class
            if(message.startsWith("/connect ")){
                String[] messageSplit = message.split(" ");
                InetSocketAddress newUserSocketAddress = new InetSocketAddress(messageSplit[1], Integer.parseInt(messageSplit[2]));
                client.sendMessage(new HelloMessage(clientUser), newUserSocketAddress);
            } else if (message.startsWith("/list")) {
                System.out.println("KNOWN USERS");
                List<User> users = client.getKnownUsersList();
                for(User u : users){System.out.println(u.toString());}
            } else if (message.startsWith("/HLO")) {
                System.out.println("SEND HLO");
                client.sendMessageToAll(new HelloMessage(clientUser));
            } else {
                client.sendMessageToAll(new ChatMessage(clientUser.getSocketAddress(), message));
            }
        }
    }

    private static void promptJoinOrHost(Scanner scanner, Client client, User clientUser, InetSocketAddress serverManagerSocketAddress) {
        // Contact server manager.
        client.sendMessage(new HelloMessage(clientUser), serverManagerSocketAddress);

        // Join or host a room.
        System.out.println("\"join\" or \"host\"");
        String mode = scanner.nextLine();
        if(mode.toLowerCase().equals("join")){
            client.sendMessage(new RequestRoomListMessage(clientUser), serverManagerSocketAddress);
        }
        else if(mode.toLowerCase().startsWith("host"))
        {
            System.out.print("Enter room name to host: ");
            String roomName = scanner.nextLine();
            client.sendMessage(new HostRoomMessage(clientUser.getSocketAddress(), roomName), serverManagerSocketAddress);
            client.setIsHost(true);
            System.out.println("Hosting room \""+ roomName +"\"");
        }
        else
        {
            System.exit(0);
        }

        client.sendMessage(new ByeMessage(clientUser.getSocketAddress()), serverManagerSocketAddress);
    }

    /**
     * Prompts the user for their user information.
     * @param scanner
     * @return
     * @throws UnknownHostException
     */
    private static User promptClientInfo(Scanner scanner) throws UnknownHostException {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your port number: ");
        int clientPort = Integer.parseInt(scanner.nextLine());
        return new User(username, new InetSocketAddress(InetAddress.getLocalHost(), clientPort));
    }

    /**
     * Prompts the user for the server manager information
     * @param scanner
     * @return
     * @throws UnknownHostException
     */
    private static InetSocketAddress promptServerManagerAddress(Scanner scanner) throws UnknownHostException {
        System.out.print("Enter server address: ");
        String serverIpAddress = scanner.nextLine();
        System.out.print("Enter server port: ");
        int serverPort = Integer.parseInt(scanner.nextLine());
        return new InetSocketAddress(serverIpAddress, serverPort);
    }
}
