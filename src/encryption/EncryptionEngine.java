package encryption;

import javax.crypto.KeyGenerator;
import javax.crypto.Cipher;
import java.security.SecureRandom;
import java.security.Key;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import java.security.GeneralSecurityException;
import java.io.IOException;


/**
 * This class sets up and operates a stream that
 * takes in bytes and returns them in and AES encrypted form
 * this class sets up a key that cannot be changed afterwards.
 */
public class EncryptionEngine {

    /*
     * Holds the size of the keys to use
     */
    public static final int KEY_SIZE = 128;

    /*
     * Holds the operation mode of the ciphers
     */
    public static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    /*
     * The bytes of the initialization vector of the encryption
     */
    protected static final byte[] ivBytes = {10, 10, 19, 94, 3, 56, 19, 120, 20, 1, 77, 69, 9, 14, 99, 111};

    /*
     * The initialization vector of the encryption
     */
    protected static final IvParameterSpec iv = new IvParameterSpec(ivBytes);

    /*
     * Key to encrypt/decrypt with
     */
    protected Key key;

    /*
     * The cipher to encrypt with
     */
    protected Cipher encryptionCipher;

    /*
     * The cipher to decrypt with
     */
    protected Cipher decryptionCipher;

    /**
     * Creates a new EncryptionEngine. A key is created, seeded with the given userKey.
     * Then an encryption and decryption cipher are set up with that key and the given 
     * mode of operation.
     * @param userKey The string to seed the pseudorandom key generator with
     * @throws GeneralSecurityException if a fatal error occurs
     * @throws IOException if a fatal error occurs
     */
    public EncryptionEngine(String userKey) throws GeneralSecurityException, IOException{
        //SecureRandom rng = new SecureRandom(userKey.getBytes("UTF-8")); //Use key as source of randomness
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG");
        rng.setSeed(userKey.getBytes("UTF-8"));
        KeyGenerator keygen = KeyGenerator.getInstance("AES"); //Make a generator of AES keys
        keygen.init(KEY_SIZE, rng); //Set key size and source of randomness to the key
        key = new SecretKeySpec(keygen.generateKey().getEncoded(), "AES");
        encryptionCipher = Cipher.getInstance(ALGORITHM);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key, iv);
        decryptionCipher = Cipher.getInstance(ALGORITHM);
        decryptionCipher.init(Cipher.DECRYPT_MODE, key, iv);
    }

    /**
     * Takes in a byte array and returns it after encrypting it with the set key
     * @param plaintext the bytes to encrypt
     * @return byte[] the bytes after encryption
     */
    public byte[] encrypt(byte[] plaintext){
        try{
            return encryptionCipher.doFinal(plaintext);
        } catch (GeneralSecurityException gse){
            gse.printStackTrace();
            return new byte[0];
        }
    }

    /**
     * Takes in an encrypted byte array and returns it after decrypting it with the set key
     * @param plaintext the bytes to decrypt
     * @return byte[] the bytes after decryption
     */
    public byte[] decrypt(byte[] ciphertext){
        try {
            return decryptionCipher.doFinal(ciphertext);
        } catch (IllegalBlockSizeException ibse){
            ibse.printStackTrace();
            return new byte[0];
        } catch (BadPaddingException bpe){
            bpe.printStackTrace();
            return new byte[0];
        }
    }
 
}