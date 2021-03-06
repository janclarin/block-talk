package sockets;

import helpers.MessageReadHelper;
import models.messages.Message;
import encryption.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
    private final List<SocketHandlerListener> listeners = new ArrayList<>();

    /**
     * OutputStream of socket
     */
    private OutputStream out;

    /**
     * Indicates whether or not to streaming input from Socket.
     */
    private boolean continueRunning = true;

    /**
     * Indicates whether or not to this SocketHandler is in server mode
     */
    private boolean serverMode = false;

    /**
     * The encryption engine for decrypting recieved messages
     */
    private final EncryptionEngine encryptionEngine;

    /**
     * Constructs a SocketHandler with a given Socket.
     *
     * @param socket The socket to manage.
     * @param listener The Client listening to this SocketHandler
     * @param encryptionEngine The encryptionEngine to decrypt messages with
     */
    public SocketHandler(final Socket socket, final SocketHandlerListener listener, final EncryptionEngine encryptionEngine) {
        this.socket = socket;
        this.listeners.add(listener);
        this.encryptionEngine = encryptionEngine;
    }

    @Override
    public void run() {
        try {
            InputStream incomingStream = socket.getInputStream();

            while (continueRunning) {
                Message message;
                if(serverMode){
                    message = MessageReadHelper.readNextMessage(incomingStream);
                }
                else {
                    message = MessageReadHelper.readNextEncryptedMessage(incomingStream, encryptionEngine);
                }
                notifyMessageReceived(message);
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Registers the listener to receive notifications.
     *
     * @param listener The listener to receive notifications.
     */
    public void registerListener(SocketHandlerListener listener) {
        // TODO: Do not allow registering multiple times.
        listeners.add(listener);
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

        if(out == null){out = socket.getOutputStream();}
        out.write(message.toByteArray());
        out.flush();
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
        for (SocketHandlerListener listener : listeners) {
            listener.messageSent(this, message);
        }
    }

    /**
     * Notify listener that a message was received.
     *
     * @param message The message that was received.
     */
    private void notifyMessageReceived(Message message) {
        for (SocketHandlerListener listener : listeners) {
            listener.messageReceived(this, message);
        }
    }

    /**
     * Safely disconnect and end this SocketHandler
     */
    public void shutdown(){
        continueRunning = false;
    }

    /**
     * Sets the serverMode flag which indicates if this conection is with a ServerManager
     * @param serverMode The new serverMode state
     */
    public void setServerMode(boolean serverMode){
        this.serverMode = serverMode;
    }

    /**
     * Return true if this socketHandler is connected to a ServerManager
     * @return boolean the serverMode variable
     */
    public boolean getServerMode(){
        return this.serverMode;
    }
}
