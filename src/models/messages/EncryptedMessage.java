package models.messages;

import java.net.InetSocketAddress;
import java.util.Base64;

public class EncryptedMessage extends Message {

    private final byte[] ciphertext;

    public EncryptedMessage(final InetSocketAddress senderSocketAddress, final byte[] ciphertext) {
        super(senderSocketAddress);
        this.ciphertext = ciphertext;
    }
    public EncryptedMessage(final InetSocketAddress senderSocketAddress, final String ciphertext) {
        super(senderSocketAddress);
        this.ciphertext = Base64.getDecoder().decode(ciphertext);
    }

    public byte[] getCiphertext() {
        return ciphertext;
    }

    @Override
    protected String getData() {
        return new String(Base64.getEncoder().encode(ciphertext));
    }

    @Override
    public String toString() {
        return new String(Base64.getEncoder().encode(ciphertext));
    }
}
