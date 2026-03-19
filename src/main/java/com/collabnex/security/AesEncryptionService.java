package com.collabnex.security;

import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * Service for AES-256 symmetric encryption and decryption.
 * Can be used for encrypting sensitive data at rest (e.g., documents, tokens).
 *
 * <p>Uses ECB mode by default. For production use with large or structured data,
 * consider switching to GCM or CBC with IV.</p>
 */
@Service
public class AesEncryptionService {

    private static final String AES = "AES";

    /**
     * Generates a new random AES-256 secret key.
     *
     * @return a new {@link SecretKey}
     * @throws Exception if the AES algorithm is not available
     */
    public SecretKey generateKey() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance(AES);
        generator.init(256);
        return generator.generateKey();
    }

    /**
     * Encrypts the given plaintext bytes using the provided AES key.
     *
     * @param data the plaintext data to encrypt
     * @param key  the AES secret key
     * @return the encrypted byte array
     * @throws Exception if encryption fails
     */
    public byte[] encrypt(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * Decrypts the given ciphertext bytes using the provided AES key.
     *
     * @param encrypted the encrypted data
     * @param key       the AES secret key used for encryption
     * @return the decrypted plaintext byte array
     * @throws Exception if decryption fails
     */
    public byte[] decrypt(byte[] encrypted, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encrypted);
    }

    /**
     * Encodes an AES secret key to a Base64 string for storage.
     *
     * @param key the AES secret key
     * @return Base64-encoded string representation of the key
     */
    public String encodeKey(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    /**
     * Decodes a Base64-encoded string back into an AES secret key.
     *
     * @param encodedKey the Base64-encoded key string
     * @return the reconstructed {@link SecretKey}
     */
    public SecretKey decodeKey(String encodedKey) {
        byte[] decoded = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decoded, AES);
    }
}
