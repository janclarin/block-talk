package chatroom;

import models.User;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import models.Message;

/**
 * Handles a connection to a chat room socket.
 * Should notify everything else aside from new connections.
 */
public class SocketHandler implements Runnable {
    /**
     * Socket to manage.
     */
    private final Socket socket;

    /**
     * Listener to notify.
     */
    private final SocketHandlerListener listener;

    /**
     * Indicates whether or not to streaming input from Socket.
     */
    private boolean continueRunning = true;

    /**
     * User on the end of this socket.
     */
    private User user;

    /**
     * Constructs a SocketHandler with a given Socket.
     *
     * @param socket The socket to manage.
     */
    public SocketHandler(final Socket socket, final SocketHandlerListener listener) {
        this.socket = socket;
        this.listener = listener;
        user = new User("Port" + socket.getPort(), socket.getInetAddress(), socket.getPort());

    }

    @Override
    public void run() {
        InputStream incomingStream = null;
        try {
            incomingStream = socket.getInputStream();

            Message message;
            while (continueRunning) {
                while(incomingStream.available()<Message.HEADER_SIZE){}
                byte[] header = new byte[Message.HEADER_SIZE];
                incomingStream.read(header);
                message = new Message(header, new byte[0]);
                byte[] data = new byte[message.parseSize(header)];
                while(incomingStream.available()<data.length){}
                incomingStream.read(data);
                message.setData(new String(data));
                if(message.getData().startsWith("HLO") && !message.getData().split(" ").equals(user.getUsername())){
                    this.user = new User(message.getData().split(" ")[1], socket.getInetAddress(),Integer.parseInt(message.getData().split(" ")[2]));
                }
                notifyMessageReceived(user, message);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message through the socket.
     *
     * @param message The message to send.
     * @throws IOException Thrown when the connection has closed and cannot send.
     */
    public void sendMessage(Message message) throws IOException {
        if (isConnectionClosed()) {
            throw new IOException("Connection has been terminated.");
        }

        try {
            OutputStream outgoingStream = socket.getOutputStream();
            outgoingStream.write(message.toByteArray());
            outgoingStream.flush();
            // TODO: Handle when the connection terminates.
            //notifyMessageSent(message); // Notify listeners that the message was successfully sent.
        } catch (IOException e) {
            System.err.println("IOException: " + e);
            e.printStackTrace();
            // TODO: Notify listeners that sending the message failed.
        }
    }

    /**
     * Indicates whether or not the socket has been closed or is disconnected.
     *
     * @return Boolean indicating if the socket has been closed or disconnected.
     */
    public boolean isConnectionClosed() {
        return !socket.isConnected() || socket.isClosed();
    }

    /**
     * Notify listener that a message was sent.
     *
     * @param recipient The recipient of the sent message.
     * @param message The message that was sent.
     */
    private void notifyMessageSent(User recipient, Message message) {
        listener.messageSent(this, recipient, message);
    }

    /**
     * Notify listener that a message was received.
     *
     * @param sender The sender of the received message.
     * @param message The message that was received.
     */
    private void notifyMessageReceived(User sender, Message message) {
        listener.messageReceived(this, sender, message);
    }

    /**
     * Set the user on the end of this socket.
     *
     * @param peer The remote user.
     */
    private void setUser(User peer) {
        this.user = peer;
    }

    /**
     * Get the user on the end of this socket.
     *
     * @return User the user object of the peer
     */
    private User setUser() {
        return this.user;
    }

    public void shutdown(){
        continueRunning = false;
    }


}
