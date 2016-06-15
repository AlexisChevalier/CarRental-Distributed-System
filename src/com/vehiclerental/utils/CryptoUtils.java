/**
 * CarRental
 *
 * This file provides multiple hashing/cryptography abstraction methods
 */

package com.vehiclerental.utils;

import org.bouncycastle.util.encoders.Hex;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class CryptoUtils {

    //Encryption algorithm used (Passphrase-based encryption)
    private static final String ENCRYPTION_ALGORITHM = "PBEWITHSHA256AND128BITAES-CBC-BC";

    //If those three variables are changed, the encrypted values in the database won't be readable because we need those elements to decrypt the encrypted values
    private static final char[] ENCRYPTION_PASSPHRASE = "6afjVv5Lm4c64Z5p4EWD74n2oFI4Km5u".toCharArray();
    private static final byte[] ENCRYPTION_SALT = "8gc8JNYG1kDKo8XMP7LG823Lt93e35h8".getBytes();
    private static final int ENCRYPTION_ITERATION_COUNT = 20;

    /**
     * Compares a plain text value and its supposedly corresponding SHA256 hash
     *
     * @param hash SHA256 hash
     * @param clearValue plain text value
     * @return true if the SHA256 hashed plain text value is the same than the given SHA256 hash
     * @throws UnsupportedEncodingException if the encoding system fails
     * @throws NoSuchAlgorithmException if the encryption system fails
     */
    public static boolean AreHashAndClearValueEqual(String hash, String clearValue) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String hashedValue = Sha256Hash(clearValue);
        return hashedValue.equals(hash);
    }

    /**
     * Hash a given plain text value using SHA256 (encoded in hexadecimal)
     *
     * @param clearMessage plain text message
     * @return hashed string
     * @throws NoSuchAlgorithmException if the encryption system fails
     * @throws UnsupportedEncodingException if the encoding system fails
     */
    public static String Sha256Hash(String clearMessage) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = md.digest(clearMessage.getBytes("UTF-8"));

        return byteArrayToHexString(hashedBytes);
    }

    /**
     * Encodes a byte array to an hexadecimal string (uses the BouncyCastle crypto library)
     *
     * @param arrayBytes the byte array
     * @return the encoded string
     */
    private static String byteArrayToHexString(byte[] arrayBytes) {
        return Hex.toHexString(arrayBytes);
    }

    /**
     * Decodes an hexadecimal string to a byte array (uses the BouncyCastle crypto library)
     *
     * @param hexString the hex string
     * @return the encoded string
     */
    private static byte[] hexStringToByteArray(String hexString) {
        return Hex.decode(hexString);
    }

    /**
     * Decrypts an encrypted (using AES256) string (encoded in hexadecimal)
     * @param hexString hexadecimal-encoded AES256 encrypted string
     * @return plain text string
     * @throws Exception if the encryption system fails
     */
    public static String decrypt(String hexString) throws Exception {
        Cipher encryptCipher = buildCipher(Cipher.DECRYPT_MODE);

        return new String(encryptCipher.doFinal(hexStringToByteArray(hexString)), "UTF-8");
    }

    /**
     * Encrypts a plain text string with AES256 and encode the result in hexadecimal
     * @param data plain text string
     * @return AES256 encrypted and Hex encoded string
     * @throws Exception if the encryption system fails
     */
    public static String encrypt(String data) throws Exception {
        Cipher encryptCipher = buildCipher(Cipher.ENCRYPT_MODE);

        return byteArrayToHexString(encryptCipher.doFinal(data.getBytes("UTF-8")));
    }

    /**
     * Builds the java cipher using the BouncyCastle crypto library)
     *
     * @param encryptMode Cipher.DECRYPT_MODE or Cipher.ENCRYPT_MODE
     * @return the AES256 encrypt or decrypt cipher
     * @throws InvalidKeySpecException if the key specification is invalid
     * @throws InvalidAlgorithmParameterException if the algorithm is not supported
     * @throws InvalidKeyException if the key is invalid
     * @throws NoSuchAlgorithmException if the encryption system fails
     * @throws NoSuchPaddingException if the algorithm padding parameter is not supported
     */
    private static Cipher buildCipher(int encryptMode) throws InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
        //Create the key specification
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(ENCRYPTION_SALT, ENCRYPTION_ITERATION_COUNT);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(ENCRYPTION_PASSPHRASE);

        //Configure the key
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ENCRYPTION_ALGORITHM);
        SecretKey secretKey = secretKeyFactory.generateSecret(pbeKeySpec);

        //Initialise the cipher
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(encryptMode, secretKey, pbeParamSpec);

        return cipher;
    }
}
