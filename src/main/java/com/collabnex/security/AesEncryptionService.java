package com.collabnex.security;

import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Service
public class AesEncryptionService {

    private static final String AES = "AES";

    public SecretKey generateKey() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance(AES);
        generator.init(256);
        return generator.generateKey();
    }

    public byte[] encrypt(byte[] data, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    public byte[] decrypt(byte[] encrypted, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encrypted);
    }

    public String encodeKey(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public SecretKey decodeKey(String encodedKey) {
        byte[] decoded = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decoded, AES);
    }
}
