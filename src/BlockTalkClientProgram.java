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

import java.security.GeneralSecurityException;
import java.io.IOException;

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

    @Override
    public void listProcessed(boolean isHosting) {
        if(isHosting) {
            System.out.println("Hosting new room.");
        }
        else {
            System.out.println("Joining existing room.");
        }
    }

    /**
     * Main function to participate in chat rooms.
     *
     * @param args Command line arguments.
     * @throws UnknownHostException Invalid host.
     */
    public static void main(String[] args) throws UnknownHostException, IOException, GeneralSecurityException {
        BlockTalkClientProgram program = new BlockTalkClientProgram();
        Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

        // Prompt user for user information
        User clientUser = promptClientInfo(scanner);

        // Start client.
        Client client = new Client(clientUser, program);
        new Thread(client).start();

        // Prompt user for desired key/room
        promptKey(client, scanner);

        // Prompt user for server information
        InetSocketAddress serverManagerSocketAddress = promptServerManagerAddress(scanner);
        client.setServerManagerAddress(serverManagerSocketAddress);

        //Interact with the server - will end with user in a room
        requestRoomList(client, clientUser, serverManagerSocketAddress);

        // Listen for user input.
        String message = "";
        while(!message.equals("/q")){
            message = scanner.nextLine();
            //TODO: Move this logic to another class
            if(message.startsWith("/connect ")){
                String[] messageSplit = message.split(" ");
                InetSocketAddress newUserSocketAddress = new InetSocketAddress(messageSplit[1], Integer.parseInt(messageSplit[2]));
                client.sendMessage(new HelloMessage(clientUser), newUserSocketAddress, true);
            } else if (message.startsWith("/list")) {
                System.out.println("KNOWN USERS");
                List<User> users = client.getKnownUsersList();
                for(User u : users){System.out.println(u.toString());}
            } else if (message.startsWith("/HLO")) {
                System.out.println("SEND HLO");
                client.sendMessageToAll(new HelloMessage(clientUser));
            } else if (message.startsWith("/ODR")) {
                Message msgA = new ChatMessage(clientUser.getSocketAddress(), client.timestamp(), "A");
                Message msgB = new ChatMessage(clientUser.getSocketAddress(), client.timestamp(), "B");
                Message msgC = new ChatMessage(clientUser.getSocketAddress(), client.timestamp(), "C");
                client.sendMessageToAll(msgC);
                client.sendMessageToAll(msgB);
                client.sendMessageToAll(msgA);
            } else if (message.startsWith("/rank")) {
                for (User user : client.getUserRankingOrderList()) { System.out.println(user); }
            } else {
                client.sendMessageToAll(new ChatMessage(clientUser.getSocketAddress(),client.timestamp(), message));
            }
        }
    }

    private static void requestRoomList(Client client, User clientUser, InetSocketAddress serverManagerSocketAddress){
        // Contact server manager.
        client.sendMessage(new HelloMessage(clientUser), serverManagerSocketAddress, false);
        client.sendMessage(new RequestRoomListMessage(clientUser), serverManagerSocketAddress, false);
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

    /**
     * Prompts the user for the a key seed to use
     * @param scanner
     * @return String the key that was chosen
     * @throws UnknownHostException
     */
    private static String promptKey(Client client, Scanner scanner)  throws IOException, GeneralSecurityException {
        System.out.print("Enter your room key: ");
        String key = scanner.nextLine();
        client.setKey(key);
        return key;
    }
}
