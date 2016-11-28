package chatroom;

import models.User;
import models.messages.*;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a client of the system and will
 * allow the user to create or connect to rooms and communicate
 * with the server and other clients.
 *
 * @author Clinton Cabiles
 * @author Jan Clarin
 * @author Riley Lahd
 */
public class Client implements Runnable, SocketHandlerListener {
    /**
     * User associate with this client.
     */
    private User clientUser;

    /**
     * Is this client currently a room host.
     */
    private boolean isHost = false;

    private final Map<SocketHandler, User> socketHandlerUserMap = new HashMap<>();

    /**
     * Thread pool for SocketHandlers.
     */
    private ExecutorService socketHandlerThreadPool;

    /**
     * Listener for chat room events.
     */
    private ClientListener listener;

    /**
     * Server socket for incoming connections.
     */
    private ServerSocket serverSocket;

    /**
     * Determines if the Client should continue listening for requests.
     */
    private boolean continueRunning = true;

    /**
     * Creates a new Client with the given models.
     *
     * @param clientUser Client user information.
     * @param listener   Listener to notify when certain events occur.
     */
    public Client(final User clientUser, final ClientListener listener) {
        this.clientUser = clientUser;
        this.listener = listener;
    }

    @Override
    public void messageSent(SocketHandler recipientSocketHandler, Message message) {
        User recipient = socketHandlerUserMap.get(recipientSocketHandler);

        // Notify listener that a message was sent.
        listener.messageSent(recipient, message);
    }

    @Override
    public void messageReceived(SocketHandler senderSocketHandler, Message message) {
        if (message instanceof HelloMessage) {
            handleHelloMessage(senderSocketHandler, (HelloMessage) message);
        }
        else if (message instanceof UserInfoMessage) {
            handleUserInfoMessage((UserInfoMessage) message);
        }
        else if (message instanceof ListRoomsMessage) {
            handleListRoomsMessage((ListRoomsMessage) message);
        }
        else if (message instanceof YourInfoMessage) {
            handleYourInfoMessage((YourInfoMessage) message);
        }

        // Notify listener that a message was received.
        User sender = socketHandlerUserMap.get(senderSocketHandler);
        listener.messageReceived(sender, message);
    }

    private void handleHelloMessage(SocketHandler senderSocketHandler, HelloMessage message) {
        User sender = message.getSender();
        if (isHost) {
            // Send new client information to all clients.
            sendMessageToAll(new UserInfoMessage(clientUser, sender));
            // Send message to the new client.
            sendMessage(new HelloMessage(clientUser), senderSocketHandler);
        }
        socketHandlerUserMap.put(senderSocketHandler, sender);
    }

    private void handleUserInfoMessage(UserInfoMessage message) {
        User messageUser = message.getUser();
        if (!clientUser.equals(messageUser)) {
            try {
                SocketHandler newSocketHandler = openConnection(messageUser.getSocketAddress());
                socketHandlerUserMap.put(newSocketHandler, messageUser);
                sendMessage(new HelloMessage(clientUser), newSocketHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleListRoomsMessage(ListRoomsMessage message) {
        /*
        TODO:
        try{
            String[] rooms = message.getData().substring(4).split("\n");
            System.out.println("Joining room: "+rooms[0].split(" @ ")[0]);
            User newUser = new User("NewUser", InetAddress.getByName(rooms[0].split(" @ ")[1].split(":")[0].replace("/","")), Integer.parseInt(rooms[0].split(":")[1]));
            sendMessage(new Message(serverSocket.getInetAddress(),clientPort,"HLO "+clientUsername+" "+clientPort), newUser);
        }catch(UnknownHostException e){
            System.out.println("FAILED TO JOIN ROOM");
            e.printStackTrace();
        }
        */
    }

    private void handleYourInfoMessage(YourInfoMessage message) {
        this.clientUser = message.getUser();
    }

    /**
     * Starts listening to serverSocket.
     */
    @Override
    public void run() {
        socketHandlerThreadPool = Executors.newCachedThreadPool();

        try {
            serverSocket = new ServerSocket(clientUser.getPort());

            while (continueRunning) {
                Socket socket = serverSocket.accept();
                SocketHandler socketHandler = new SocketHandler(socket, this);
                socketHandlerThreadPool.execute(socketHandler);
            }

            socketHandlerThreadPool.shutdown();
            serverSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to all known recipients through their SocketHandlers.
     *
     * @param message The message as a String.
     */
    public void sendMessageToAll(Message message) {
        for (SocketHandler recipientSocketHandler : socketHandlerUserMap.keySet()) {
            sendMessage(message, recipientSocketHandler);
        }
    }

    /**
     * Sends a message to the given socket.
     *
     * @param message   The message as a Message object.
     * @param recipientSocketHandler The recipient's SocketHandler.
     */
    public void sendMessage(Message message, SocketHandler recipientSocketHandler) {
        try {
            recipientSocketHandler.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message, InetSocketAddress recipientSocketAddress) {
        try {
            SocketHandler recipientSocketHandler = null;

            // Find matching socket handler with the recipientSocketAddress.
            for (SocketHandler socketHandler : socketHandlerUserMap.keySet()) {
                // Compare IP address and ports.
                if (socketHandler.getRemoteSocketAddress().equals(recipientSocketAddress)) {
                    recipientSocketHandler = socketHandler;
                    break;
                }
            }

            // If there was no match, open a new socket handler connection.
            if (recipientSocketHandler == null) {
                recipientSocketHandler = openConnection(recipientSocketAddress);
            }

            // Send the message with the socket handler pointing to the recipientSocketAddress.
            sendMessage(message, recipientSocketHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a string form of the current known users map
     *
     * @return String The list of known users and their SocketHandlers
     */
    public List<User> getKnownUsersList(){
        return new ArrayList<>(socketHandlerUserMap.values());
    }

    /**
     * Set isHost variable
     *
     * @param isHost True if the this client is hosting a room
     */
    public void setIsHost(boolean isHost){
        this.isHost = isHost;
    }

    /**
     * Get isHost variable
     *
     * @return boolean True if the this client is hosting a room
     */
    public boolean isHost(){
        return this.isHost;
    }

    /**
     * Opens a connection with given user and sends a HLO
     * @param userSocketAddress The socket address of the user to open a connection with
     * @return SocketHandler The socketHandler connected to that user
     */
    private SocketHandler openConnection(InetSocketAddress userSocketAddress) throws IOException
    {
        Socket socket = new Socket(userSocketAddress.getAddress(), userSocketAddress.getPort());
        SocketHandler socketHandler = new SocketHandler(socket, this);
        socketHandlerThreadPool.execute(socketHandler);
        return socketHandler;
    }
}