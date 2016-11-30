package server;

import helpers.MessageReadHelper;
import models.User;
import models.messages.HostRoomMessage;
import models.messages.Message;
import models.messages.RequestRoomListMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ClientServerConnectionRelay implements ClientConnectionListener {

    private List<Socket> serverSockets;

    public ClientServerConnectionRelay(List<Socket> serverSockets) {
        this.serverSockets = serverSockets;
    }

    public Message sendMessage(Socket socket, Message outgoing) throws IOException {
        synchronized (socket) {
            OutputStream serverOutputStream = socket.getOutputStream();

            serverOutputStream.write(outgoing.toByteArray());
            serverOutputStream.flush();

            // Return the response.
            return MessageReadHelper.readNextMessage(socket.getInputStream());
        }
    }

    @Override
    public Message messageReceived(Message message) {
        // Forward received message to each server.
        Message responseMessage = null;
        for (Socket serverSocket : serverSockets) {
            try {
                // TODO: Ensure that every response is the same from every server.
                responseMessage = sendMessage(serverSocket, message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseMessage;
    }
}
