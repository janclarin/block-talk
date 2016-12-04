package models.messages;

import java.net.InetSocketAddress;
import java.util.Base64;

/**
* A message class for storing other messages in an encryted form.
* Uses Base64 encoding to store the byte[] data as a String.
*/
public class EncryptedMessage extends Message {

    private final byte[] ciphertext;

    /**
     * Constructs an encrypted message with the bytes of an encrypted message
     * @param senderSocketAddress The sender information to store in the header
     * @param ciphertext The bytes of the encrypted data
     */
    public EncryptedMessage(final InetSocketAddress senderSocketAddress, final byte[] ciphertext) {
        super(senderSocketAddress);
        this.ciphertext = ciphertext;
    }
    /**
     * Creates an encrypted message with a Base64 encoded string
     * @param senderSocketAddress The sender information to store in the header
     * @param ciphertext The Base64 encoded bytes of the encrypted data
     */
    public EncryptedMessage(final InetSocketAddress senderSocketAddress, final String ciphertext) {
        super(senderSocketAddress);
        this.ciphertext = Base64.getDecoder().decode(ciphertext);
    }

    /**
     * Returns the byte[] representing the encrypted data
     * @return byte[] The encrypted body of the message
     */
    public byte[] getCiphertext() {
        return ciphertext;
    }

    @Override
    protected String getData() {
        return new String(Base64.getEncoder().encode(ciphertext));
    }
}
