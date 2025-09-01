package com.johnnyjansen.bank_account.infrastructure.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AES256Encryptor {

    @Value("${aes.secret-key}")
    private String secretKeyConfig;

    private SecretKeySpec secretKey;

    @PostConstruct
    private void init() {
        // Check if the key is exactly 32 characters long (256 bits)
        if (secretKeyConfig.length() != 32) {
            throw new IllegalArgumentException("AES key must be exactly 32 characters (256 bits) long.");
        }
        this.secretKey = new SecretKeySpec(secretKeyConfig.getBytes(), "AES");
    }

    public String encrypt(String originalText) throws Exception {
        // Generate a random 16-byte IV
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Encrypt the text
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encrypted = cipher.doFinal(originalText.getBytes());

        // Combine IV with the encrypted text
        byte[] ivPlusEncrypted = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, ivPlusEncrypted, 0, iv.length);
        System.arraycopy(encrypted, 0, ivPlusEncrypted, iv.length, encrypted.length);

        // Return as Base64 for easier storage in DB or JSON
        return Base64.getEncoder().encodeToString(ivPlusEncrypted);
    }

    public String decrypt(String encryptedText) throws Exception {
        byte[] ivPlusEncrypted = Base64.getDecoder().decode(encryptedText);

        // Extract the IV from the first 16 bytes
        byte[] iv = new byte[16];
        byte[] encrypted = new byte[ivPlusEncrypted.length - 16];

        System.arraycopy(ivPlusEncrypted, 0, iv, 0, 16);
        System.arraycopy(ivPlusEncrypted, 16, encrypted, 0, encrypted.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Decrypt
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted);
    }
}
