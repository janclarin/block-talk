package chatroom;

import helpers.MessageReadHelper;
import models.messages.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

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
     * OutputStream of socket
     */
    private OutputStream out;

    /**
     * Indicates whether or not to streaming input from Socket.
     */
    private boolean continueRunning = true;

    /**
     * Constructs a SocketHandler with a given Socket.
     *
     * @param socket The socket to manage.
     */
    public SocketHandler(final Socket socket, final SocketHandlerListener listener) {
        this.socket = socket;
        this.listener = listener;

    }

    @Override
    public void run() {
        try {
            InputStream incomingStream = socket.getInputStream();

            while (continueRunning) {
                Message message = MessageReadHelper.readNextMessage(incomingStream);
                notifyMessageReceived(message);
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
            if(out == null){out = socket.getOutputStream();}
            out.write(message.toByteArray());
            out.flush();
            // TODO: Handle when the connection terminates.
            //notifyMessageSent(message); // Notify listeners that the message was successfully sent.
        } catch (IOException e) {
            System.err.println("IOException: " + e);
            e.printStackTrace();
            // TODO: Notify listeners that sending the message failed.
        }
    }

    /**
     * Returns the remote socket address of the socket.
     *
     * @return The remote socket address.
     */
    public InetSocketAddress getRemoteSocketAddress() {
        return (InetSocketAddress) socket.getRemoteSocketAddress();
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
     * @param message The message that was sent.
     */
    private void notifyMessageSent(Message message) {
        listener.messageSent(this, message);
    }

    /**
     * Notify listener that a message was received.
     *
     * @param message The message that was received.
     */
    private void notifyMessageReceived(Message message) {
        listener.messageReceived(this, message);
    }

    public void shutdown(){
        continueRunning = false;
    }
}
