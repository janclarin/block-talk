package encryption;

import javax.crypto.KeyGenerator;
import javax.crypto.Cipher;
import java.security.SecureRandom;
import java.security.Key;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
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

    public EncryptionEngine(String userKey) throws GeneralSecurityException, IOException{
        //SecureRandom rng = new SecureRandom(userKey.getBytes("UTF-8")); //Use key as source of randomness
        SecureRandom rng = SecureRandom.getInstance("SHA1PRNG");
        rng.setSeed(userKey.getBytes("UTF-8"));
        KeyGenerator keygen = KeyGenerator.getInstance("AES"); //Make a generator of AES keys
        keygen.init(KEY_SIZE, rng); //Set key size and source of randomness to the key
        key = new SecretKeySpec(keygen.generateKey().getEncoded(), "AES");
        encryptionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key, iv);
        decryptionCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        decryptionCipher.init(Cipher.DECRYPT_MODE, key, iv);
    }

    public byte[] encrypt(byte[] plaintext) throws GeneralSecurityException {
        return encryptionCipher.doFinal(plaintext);
    }

    public byte[] decrypt(byte[] ciphertext) throws GeneralSecurityException {
        return decryptionCipher.doFinal(ciphertext);
    }
 
}