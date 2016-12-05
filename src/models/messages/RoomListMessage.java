package models.messages;

import models.MessageType;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;

public class RoomListMessage extends Message {

    private final List<byte[]> entries;

    public RoomListMessage(final InetSocketAddress senderSocketAddress, final List<byte[]> entries) {
        super(senderSocketAddress);
        this.entries = entries;
    }

    public RoomListMessage(final InetSocketAddress senderSocketAddress, String entryList) {
        //This constructor builds the entries from a string
        super(senderSocketAddress);
        this.entries = buildList(entryList);
    }

    public List<byte[]> getEntries() {
        return entries;
    }

    private List<byte[]> buildList(String listString) {
        List<byte[]> list = new ArrayList<byte[]>();
        listString = listString.replace(MessageType.ROOM_LIST.getProtocolCode(), "");
        String[] splitString = listString.split("\n");
        for(String encodedEntry: splitString) {
            list.add(Base64.getDecoder().decode(encodedEntry));
        }
        return list;
    }

    @Override
    protected String getData() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MessageType.ROOM_LIST.getProtocolCode());
        stringBuilder.append(" ");
        for (byte[] entry : entries) {
            //Format: LST [<encoded entry>\n]*
            stringBuilder.append(Base64.getEncoder().encode(entry));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
