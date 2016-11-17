package chatroom;

import models.User;
import models.Message;  

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
     * Username used for sending messages.
     */
    private final String clientUsername;

    /**
     * Port used for incoming requests.
     */
    private final int clientPort;

    /**
     * Socket map for retrieving the socket handler for a models.User.
     */
    private final Map<User, SocketHandler> userSocketHandlerMap = new HashMap<>();

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
     * Creates a new Client with the given models.User info.
     *
     * @param clientPort Port that other Users will connect to.
     * @param listener   Listener to notify when certain events occur.
     */
    public Client(final String clientUsername, final int clientPort, final ClientListener listener) {
        this.clientUsername = clientUsername;
        this.clientPort = clientPort;
        this.listener = listener;
    }

    @Override
    public void messageSent(SocketHandler recipientSocketHandler, User recipient, Message message) {
        notifyMessageSent(recipient, message);
    }

    @Override
    public void messageReceived(SocketHandler senderSocketHandler, User sender, Message message) {
        if(true){
            //REMOVE OLD SENDERSOCKETHANDLER ENTRY FROM HASHMAP
            //TODO: this relies on username being ignored for equality
            removeUserFromList(senderSocketHandler);
            userSocketHandlerMap.remove(sender);
            userSocketHandlerMap.put(sender, senderSocketHandler);
        }
        notifyMessageReceived(sender, message);

        // TODO: Remove auto-reply.
        //sendMessage("Hello from " + clientUsername, sender);
    }

    /**
     * Starts listening to incomingSocket. Replies to every received message with the port number.
     */
    @Override
    public void run() {
        socketHandlerThreadPool = Executors.newCachedThreadPool();

        try {
            serverSocket = new ServerSocket(clientPort);

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
        for (User recipient : userSocketHandlerMap.keySet()) {
            sendMessage(message, recipient);
        }
    }

    /**
     * Sends a message to the given socket.
     *
     * @param message   The message as a Message object.
     * @param recipient The User to send the message to.
     */
    public void sendMessage(Message message, User recipient) {
        try {
            Thread.sleep(1000); // TODO: Remove delay.
            SocketHandler socketHandler = getSocketHandler(recipient);
            socketHandler.sendMessage(message.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds the SocketHandler for a User if it exists.
     *
     * @param user The User to find the SocketHandler for.
     * @return The SocketHandler for a given user.
     * @throws IOException Thrown when there is an issue creating a new Socket if the User does not have one.
     */
    private SocketHandler getSocketHandler(User user) throws IOException {
        SocketHandler socketHandler = userSocketHandlerMap.get(user);

        if (socketHandler == null || socketHandler.isConnectionClosed()) {
            Socket socket = new Socket(user.getIpAddress(), user.getPort());
            socketHandler = new SocketHandler(socket, this);
            userSocketHandlerMap.put(user, socketHandler);
            socketHandlerThreadPool.execute(socketHandler);
        }

        return socketHandler;
    }

    /**
     * Notifies all listeners about the sent message.
     *
     * @param recipient The User who received the sent message.
     * @param message   The sent message.
     */
    private void notifyMessageSent(User recipient, Message message) {
        listener.messageSent(recipient, message);
    }

    /**
     * Notifies all listeners about the received message.
     *
     * @param sender  The User who sent the received message.
     * @param message The received message.
     */
    private void notifyMessageReceived(User sender, Message message) {
        listener.messageReceived(sender, message);
    }

    /**
     * Notifies all listeners about the new user who has joined.
     *
     * @param newUser The new user who has joined.
     */
    private void notifyUserHasJoined(User newUser) {
        listener.userHasJoined(newUser);
    }

    /**
     * Returns a string form of the current known users map
     *
     * @return String The list of known users and their SocketHandlers
     */
    public List<User> getKnownUsersList(){
        return new ArrayList<User>(userSocketHandlerMap.keySet());
    }

    /**
     * Removes a user and their socket from known users map
     *
     * @return boolean True if the user was removed successfully
     */
    public boolean removeUserFromList(User user){
        SocketHandler sh = userSocketHandlerMap.remove(user);
        return sh != null;
    }

    /**
     * Removes a user and their socket from known users map
     *
     * @return boolean True if the user was removed successfully
     */
    public boolean removeUserFromList(SocketHandler sh){
        //find the key matching this sh
        for(Map.Entry<User,SocketHandler> entry : userSocketHandlerMap.entrySet()){
            if(entry.getValue() == sh)
            {
                userSocketHandlerMap.remove(entry.getKey());
                break;
            }
        }
        return sh != null;
    }

    /**
     * Closes connection with user
     *
     * @return boolean True if the connection closes
     */
    public boolean closeConnection(User user){
        SocketHandler sh = userSocketHandlerMap.remove(user);
        sh.shutdown();
        return sh.isConnectionClosed();
    }
}