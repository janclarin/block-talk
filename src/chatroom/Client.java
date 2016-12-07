package chatroom;

import models.User;
import models.messages.*;
import models.SenderMessageTuple;
import sockets.SocketHandler;
import sockets.SocketHandlerListener;
import encryption.*;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.security.GeneralSecurityException;

import java.util.Base64;

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
     * Delay that server socket blocks for before messages can dequeue
     */
    private final static int SERVER_SOCKET_TIMEOUT = 150;

    /**
     * Maps SocketHandlers to the User of the connection.
     */
    private final Map<SocketHandler, User> socketHandlerUserMap = new HashMap<>();

    /**
     * Listener for chat room events.
     */
    private final ClientListener listener;

    /**
     * Thread pool for SocketHandlers.
     */
    private ExecutorService socketHandlerThreadPool;

    /**
     * User associated with this client.
     */
    private User clientUser;

    /**
     * Is this client currently a room host.
     */
    private boolean isHost = false;

    /**
     * Determines if the Client should continue listening for requests.
     */
    private boolean continueRunning = true;

    /**
     * Lamport timestamp for message ordering
     */
    private int timestamp = 0;

    /**
     * Priority queue for later-timestamped messages
     */
    private PriorityQueue<SenderMessageTuple> queuedMessages = new PriorityQueue<>();

    /**
     * Room member ranking order. Users added as their information is received. Used for leader election.
     */
    private List<User> userRankingOrderList = new ArrayList<>();

    /**
     * Encryption engine for encryption protocol
     */
    private EncryptionEngine encryptionEngine;

    /**
     * Room token of the room currently in
     */
    private String roomToken;

    /**
     * InetSocketAddress of the serverManager
     */
    private InetSocketAddress serverManagerAddress;

    /**
     * SocketHandler connected to current host
     */
    private SocketHandler hostSocketHandler;

    /**
     * True if there is an ongoing election
     */
    private boolean electionMode = false;

    /**
     * Number of votes received in current election.
     */
    private int leaderElectionVotesReceived;

    /**
     * Creates a new Client with the given models.
     *
     * @param clientUser Client user information.
     * @param listener   Listener to notify when certain events occur.
     */
    public Client(final User clientUser, final ClientListener listener) {
        this.clientUser = clientUser;
        this.listener = listener;

        // Add the user to the ranking list. This gets updated when connected to a room.
        this.userRankingOrderList.add(clientUser);
    }

    /**
     * Returns the list of known Users.
     *
     * @return List of known Users.
     */
    public List<User> getKnownUsersList() {
        return new ArrayList<>(socketHandlerUserMap.values());
    }

    /**
     * Sets the field indicating whether or not this client is the host of its chat room.
     *
     * @param isHost Indicates whether or not this client is the host of its chat room.
     */
    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
    }

    /**
     * Returns whether or not this client is the host of its chat room.
     *
     * @return boolean True if the this client is hosting a room
     */
    public boolean isHost() {
        return this.isHost;
    }

    /**
     * Starts listening for incoming connections. Creates a new SocketHandler for every new connection.
     */
    @Override
    public void run() {
        socketHandlerThreadPool = Executors.newCachedThreadPool();

        try {
            // Server socket for incoming connections.
            ServerSocket serverSocket = new ServerSocket(clientUser.getPort());
            serverSocket.setSoTimeout(SERVER_SOCKET_TIMEOUT);

            while (continueRunning) {
                try{
                    Socket socket = serverSocket.accept();
                    SocketHandler socketHandler = new SocketHandler(socket, this, encryptionEngine);
                    socketHandlerThreadPool.execute(socketHandler);
                }catch (SocketTimeoutException ste){
                    //simply means no connection came in that time.
                    // Allow messages to dequeue then resume blocking
                }
                dequeueMessages();
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
            sendMessage(message, recipientSocketHandler, true);
        }
    }

    /**
     * Sends a message to the given socket.
     *
     * @param message   The message as a Message object.
     * @param recipientSocketHandler The recipient's SocketHandler.
     * @param encrypt True if the message should be encrypted first
     */
    public void sendMessage(Message message, SocketHandler recipientSocketHandler, boolean encrypt) {
        if(encrypt){message = encryptMessage(message);}
        try{
            recipientSocketHandler.sendMessage(message);
        } catch (IOException ioe) {
            System.out.println("User "+socketHandlerUserMap.get(recipientSocketHandler).toString()+" is dead!");
            handleDeadUser(recipientSocketHandler, true);
        }
    }

    /**
     * Sends a message to the given socket address.
     * Tries to find an existing socket handler in the socketHandlerUserMap.
     * If it does not find one, open a new socket connection.
     *
     * @param message The message to send.
     * @param recipientSocketAddress The recipient's socket address.
     * @param encrypt True if this message should be encrypted (is to a client)
     */
    public void sendMessage(Message message, InetSocketAddress recipientSocketAddress, boolean encrypt) {
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
                recipientSocketHandler = openSocketConnection(recipientSocketAddress, !encrypt);
            }

            // Send the message with the socket handler pointing to the recipientSocketAddress.
            sendMessage(message, recipientSocketHandler, encrypt);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notifies listener that a message was successfully sent through a SocketHandler.
     *
     * @param recipientSocketHandler The SocketHandler that the message was sent through.
     * @param message The sent message.
     */
    @Override
    public void messageSent(SocketHandler recipientSocketHandler, Message message) {
        User recipient = socketHandlerUserMap.get(recipientSocketHandler);
        // Notify listener that a message was sent.
        listener.messageSent(recipient, message);
    }

    /**
     * Responds to different types of received messages and notifies listeners about the message.
     *
     * @param senderSocketHandler The SocketHandler that the message was received from.
     * @param message The received message.
     */
    @Override
    public void messageReceived(SocketHandler senderSocketHandler, Message message) {
        boolean notify = true;
        User sender = socketHandlerUserMap.get(senderSocketHandler);
        if (message instanceof HelloMessage) {
            handleHelloMessage(senderSocketHandler, (HelloMessage) message);
        }
        else if (message instanceof UserInfoMessage) {
            handleUserInfoMessage((UserInfoMessage) message);
        }
        else if (message instanceof RoomListMessage) {
            handleRoomListMessage((RoomListMessage) message);
        }
        else if (message instanceof YourInfoMessage) {
            handleYourInfoMessage((YourInfoMessage) message);
        }
        else if (message instanceof ChatMessage) {
            notify = handleChatMessage((ChatMessage) message, sender);
        }
        else if (message instanceof AckMessage) {
            //get token from server
            handleAckMessage((AckMessage) message);
        }
        else if (message instanceof UserRankOrderMessage) {
            handleUserRankOrderMessage((UserRankOrderMessage) message);
        }
        else if (message instanceof DeadUserMessage) {
            handleDeadUserMessage((DeadUserMessage) message);
        }
        else if (message instanceof ByeMessage) {
            handleByeMessage((ByeMessage) message, sender);
        }
        else if (message instanceof LeaderVoteMessage) {
            handleLeaderVoteMessage((LeaderVoteMessage) message);
        }
        else if (message instanceof LeaderMessage) {
            handleLeaderMessage((LeaderMessage) message);
        }

        if(notify){
            // Notify listener that a message was received.
            listener.messageReceived(sender, message);
        }
    }

    /**
     * Handles HelloMessages.
     * Maps the sender of the message to the SocketHandler.
     * If this client is the host, notify all other clients about the new client. Also, replies to the new client.
     *
     * @param senderSocketHandler The SocketHandler of the sender.
     * @param message The message to handle.
     */
    private void handleHelloMessage(SocketHandler senderSocketHandler, HelloMessage message) {
        User sender = message.getSender();
        if (isHost) {
            // Send new client information to all clients.
            sendMessageToAll(new UserInfoMessage(clientUser, sender));
            // Add sender to ranking order list.
            userRankingOrderList.add(sender);
            // Send hello, user rank order, and ACK with token messages to the new client.
            sendMessage(new HelloMessage(clientUser), senderSocketHandler, true);
            sendMessage(new UserRankOrderMessage(clientUser.getSocketAddress(), userRankingOrderList), senderSocketHandler, true);
            sendMessage(new AckMessage(clientUser.getSocketAddress(), "TOKEN " + roomToken), senderSocketHandler, true);
        }
        socketHandlerUserMap.put(senderSocketHandler, sender);
    }

    /**
     * Handles UserInfoMessages.
     * Ignores the message if it is from this client.
     * Otherwise, opens a new connection to the user specified in the message.
     *
     * @param message The message to handle.
     */
    private void handleUserInfoMessage(UserInfoMessage message) {
        User messageUser = message.getUser();
        if (!clientUser.equals(messageUser)) {
            try {
                SocketHandler newSocketHandler = openSocketConnection(messageUser.getSocketAddress(), false);
                socketHandlerUserMap.put(newSocketHandler, messageUser);
                sendMessage(new HelloMessage(clientUser), newSocketHandler, true);

                // Add user to ranking order list.
                userRankingOrderList.add(messageUser);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles RoomListMessages.
     * Picks the first room it can join and joins it.
     *
     * @param message
     */
    private void handleRoomListMessage(RoomListMessage message) {
        List<byte[]> entries = message.getEntries();
        String decryptedEntry = "";
        for(byte[] entry : entries){
            System.out.println("HOST ENTRY: "+new String(Base64.getEncoder().encode(entry)));
            decryptedEntry = new String(encryptionEngine.decrypt(entry));
            if(decryptedEntry.length() > 0){break;}
            System.out.println("Non matching room");
        }
        if(decryptedEntry.isEmpty()) {
            createNewRoom();
            listener.listProcessed(true);
        }
        else {
            String[] splitString = decryptedEntry.replace("/","").split(":");
            String decryptedInetAddress = splitString[0];
            int decryptedPort = Integer.parseInt(splitString[1]);
            System.out.println(decryptedInetAddress+":"+decryptedPort);
            InetSocketAddress roomAddress = new InetSocketAddress(decryptedInetAddress, decryptedPort);
            joinExistingRoom(roomAddress);
            disconnectFromServer();
            listener.listProcessed(false);
        }
    }

    /**
     * Handles YourInfoMessages.
     * Sets the clientUser of this Client to the received one. This should be the external-facing socket address.
     *
     * @param message
     */
    private void handleYourInfoMessage(YourInfoMessage message) {
        this.clientUser = message.getUser();
    }

    /**
     * Handles ChatMessages.
     *
     * Compares timestamp on incoming message with current time.
     * If message is equal or lower, send as normal. Else put into queue.
     * @param message Incoming message
     * @param sender Sender of message
     * @return boolean True if the message is allowed to continue, false otherwise
     */
    private boolean handleChatMessage(ChatMessage message, User sender) {
        if(timestamp==-1){timestamp = message.getTimestamp();} //If timestamp is non-synced, sync it
        if(message.getTimestamp() > peekTimestamp()){
            //Put it into a queue to be taken off when timestamp is higher
            queuedMessages.offer(new SenderMessageTuple(sender, message));
            return false;
        }
        timestamp(); //increment the timestamp
        return true;
    }

    /**
     * Handles AckMessage.
     *
     * Checks if this contains the room token. If so, trigger bye message to server.
     * @param message Incoming message
     * @param sender Sender of message
     * @return boolean True if the message is allowed to continue, false otherwise
     */
    private boolean handleAckMessage(AckMessage message) {
        String[] splitMessage = message.getInformation().split(" ");
        if(splitMessage[0].equals("TOKEN")){
            roomToken = splitMessage[1];
            disconnectFromServer();
            System.out.println("Token updated: "+roomToken);
        }
        return true;
    }

    /**
     * Overrides the internal userRankingOrderList with one from external source.
     *
     * @param message Incoming message.
     */
    private void handleUserRankOrderMessage(UserRankOrderMessage message) {
        userRankingOrderList = message.getUserRankOrderList();
    }

    /*
     * Handles DeadUserMessage.
     *
     * @param message Incoming message
     * @return boolean True if the message is allowed to continue, false otherwise
     */
    private boolean handleDeadUserMessage(DeadUserMessage message) {
        handleDeadUser(message.getDeadUser().getSocketAddress(), false);
        return true;
    }

    /**
     * Handles ByeMessage.
     *
     * @param message Incoming message
     * @return boolean True if the message is allowed to continue, false otherwise
     */
    private boolean handleByeMessage(ByeMessage message, User sender) {
        //Replicates a dead user message
        handleDeadUser(sender.getSocketAddress(), false);
        return true;
    }

    /**
     * Replies with its own vote. Counts leader votes.
     *
     * @param message
     */
    private void handleLeaderVoteMessage(LeaderVoteMessage message) {
        if (!electionMode) startElection();

        // Increment vote counts for self.
        this.leaderElectionVotesReceived++;

        // Check if there are enough votes for myself.
        int numVotesNeeded = (int) Math.ceil(socketHandlerUserMap.size() / 2);
        if (this.leaderElectionVotesReceived >= numVotesNeeded) {
            isHost = true; // Become the host.
            sendMessageToAll(new LeaderMessage(clientUser));
            // TODO: Notify server.
            endElection();
        }
    }

    /**
     * Handles leader message. Stops the election.
     * @param message
     */
    private void handleLeaderMessage(LeaderMessage message) {
        endElection();
    }

    /**
     * Starts the election process by sending a vote to its lowest ranked user if it is not itself.
     */
    private void startElection() {
        electionMode = true;

        // Send vote to lowest ranked user if it exists.
        if (userRankingOrderList.size() > 0) {
            User lowestRankUser = userRankingOrderList.get(0);
            // Only send vote if not self.
            if (!lowestRankUser.equals(clientUser)) {
                sendMessage(new LeaderVoteMessage(clientUser), lowestRankUser.getSocketAddress(), true);
            }
        }
    }

    /**
     * Ends the election and resets vote count.
     */
    private void endElection() {
        electionMode = false;
        leaderElectionVotesReceived = 0;
    }

    /**
     * Opens a connection with given user
     * @param userSocketAddress The socket address of the user to open a connection with
     * @param serverMode True if this connection will be with the server
     * @return SocketHandler The socketHandler connected to that user
     */
    private SocketHandler openSocketConnection(InetSocketAddress userSocketAddress, boolean serverMode) throws IOException
    {
        Socket socket = new Socket(userSocketAddress.getAddress(), userSocketAddress.getPort());
        SocketHandler socketHandler = new SocketHandler(socket, this, encryptionEngine);
        socketHandler.setServerMode(serverMode);
        socketHandlerThreadPool.execute(socketHandler);
        return socketHandler;
    }

    /**
     * Peeks at the current Lamport timestamp without incrementing it.
     * @return int The current timestamp
     */
    public int peekTimestamp(){
        return timestamp;
    }

    /**
     * Stamps a message with a timestamp, incrementing the timestamp.
     * NOTE: This message must be sent ASAP after it is stamped.
     * @return int The timestamp before incremented
     */
    public int timestamp(){
        //Dont increment if timestamp is unsynced
        if(timestamp==-1){return timestamp;}
        return timestamp++;
    }

    /**
     * Dequeues all queued messages with a timestamp lower than current timestamp
     */
    public void dequeueMessages(){
        while(queuedMessages.peek() != null && queuedMessages.peek().message.getTimestamp() <= peekTimestamp())
        {
            SenderMessageTuple popped = queuedMessages.poll();
            ChatMessage poppedMessage = popped.message;
            timestamp(); //increment timestamp
            User poppedSender = popped.sender; //TODO: get sender using message.getSenderSocketAddress()
            listener.messageReceived(poppedSender, poppedMessage);
        }
    }

    /**
     * Creates an encryptionEngine that encrypts and decrypts with the given key.
     * This should not be done while any connections are open.
     * @param key The encryption key seed to use to generate a large key
     * @throws GeneralSecurityException Thrown if there is a fatal error in building the engine
     * @throws IOException Thrown if there is a fatal error in building the engine
     */
    public void setKey(String key) throws GeneralSecurityException, IOException{
        this.encryptionEngine = new EncryptionEngine(key);
    }

    /**
     * Transforms a message into an EncryptedMessage. First the message body is encrypted,
     * then the bytes are turned into a Base64 encoded string.
     * @param message The message to encrypt
     * @return EncryptedMessage The encrypted message ready to send
     */    
    public EncryptedMessage encryptMessage(Message message) {
        byte[] plaintext = new byte[message.toByteArray().length - Message.BYTE_HEADER_SIZE];
        System.arraycopy(message.toByteArray(),Message.BYTE_HEADER_SIZE, plaintext,0,plaintext.length);
        byte[] data;
        data = encryptionEngine.encrypt(plaintext);
        return new EncryptedMessage(message.getSenderSocketAddress(), data);
    }

    /**
     * Sends a host message to the server
     */   
    private void createNewRoom() {
        setIsHost(true);
        System.out.println("SENDING HST MESSAGE.");
        byte[] encryptedInfo = encryptionEngine.encrypt(clientUser.getSocketAddress().toString().getBytes());
        System.out.println("ENCODED HOST INFO: "+new String(Base64.getEncoder().encode(encryptedInfo)));
        sendMessage(new HostRoomMessage(clientUser.getSocketAddress(), encryptedInfo), serverManagerAddress, false);
    }

    /**
     * Sends a HLO message to existing room host
     */   
    private void joinExistingRoom(InetSocketAddress roomAddress) {
        sendMessage(new HelloMessage(clientUser), roomAddress, true);
        //prep timestamp to be overwritten by new messages
        this.timestamp = -1;

    }

    /**
     * Sends a bye message to the server
     */  
    private void disconnectFromServer() {
        SocketHandler serverSocketHandler = null;
        System.out.println("SENDING BYE MESSAGE.");
        for(SocketHandler socketHandler : socketHandlerUserMap.keySet()) {
            if(socketHandler.getRemoteSocketAddress().equals(serverManagerAddress)) {
                serverSocketHandler = socketHandler;
                break;
            }
        }
        if(serverSocketHandler == null){return;}
        disconnectFromSocketHandler(serverSocketHandler);
    }

    /**
     * Sends a bye message and disconnects from given SocketHandler
     * @param serverSocketAddress The SocketHandler to be shutdown
     */  
    private void disconnectFromSocketHandler(SocketHandler socketHandler) {
        sendMessage(new ByeMessage(clientUser.getSocketAddress()), socketHandler, socketHandler.getServerMode());
        socketHandler.shutdown();
        socketHandlerUserMap.remove(socketHandler);
    }

    /**
     * Removes the userToRemove from the userRankingOrderList.
     * @param userToRemove
     */
    private void removeUserFromRankingOrder(User userToRemove) {
        userRankingOrderList.removeIf(user -> user.equals(userToRemove));
    }

    /**
     * Sets the address of the server manager
     * @param serverSocketAddress The address of the server
     */  
    public void setServerManagerAddress(InetSocketAddress serverManagerAddress) {
        this.serverManagerAddress = serverManagerAddress;
    }

    /**
     * Gets the userRankingOrderList.
     *
     * @return User ranking order list.
     */
    public List<User> getUserRankingOrderList() {
        return userRankingOrderList;
    }

    /**
     * Sets the address of the server manager
     * @param serverSocketAddress The address of the server
     */  
    private void handleDeadUser(SocketHandler deadSocketHandler, boolean broadcastDead) {
        //shut down socket
        deadSocketHandler.shutdown();
        //remove user from room map TODO: move this outside somehow so it does not happen 
        //while SendMessageToAll is active, which causes concurrent modifiction exception
        int totalUsers = socketHandlerUserMap.size();
        User deadUser = socketHandlerUserMap.remove(deadSocketHandler);
        while(socketHandlerUserMap.size() == totalUsers){}//wait for user to be removed?
        //broadcast DED message if this was a new discovery
        if(broadcastDead){sendMessageToAll(new DeadUserMessage(clientUser.getSocketAddress(),deadUser));}
        //If host, trigger election
        if(deadSocketHandler == hostSocketHandler) {
            startElection();
        }
        //Remove from user ordering
        removeUserFromRankingOrder(deadUser);
    }

    /**
     * Sets the address of the server manager
     * @param serverSocketAddress The address of the server
     */  
    private void handleDeadUser(InetSocketAddress userAddress, boolean broadcastDead) {
        SocketHandler userSocketHandler = null;
        for(SocketHandler socketHandler : socketHandlerUserMap.keySet()) {
            if(socketHandler.getRemoteSocketAddress().equals(userAddress)) {
                userSocketHandler = socketHandler;
                break;
            }
        }
        if(userSocketHandler == null){return;} //Connection is already closed
        handleDeadUser(userSocketHandler, broadcastDead);
    }
}