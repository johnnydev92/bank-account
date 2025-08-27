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
        // Verifica se a chave tem exatamente 32 caracteres
        if (secretKeyConfig.length() != 32) {
            throw new IllegalArgumentException("AES key must be exactly 32 characters (256 bits) long.");
        }
        this.secretKey = new SecretKeySpec(secretKeyConfig.getBytes(), "AES");
    }

    public String encrypt(String originalText) throws Exception {
        // Gera um IV aleatório de 16 bytes
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Criptografa o texto
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encrypted = cipher.doFinal(originalText.getBytes());

        // Junta o IV com o texto criptografado
        byte[] ivMaisEncrypted = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, ivMaisEncrypted, 0, iv.length);
        System.arraycopy(encrypted, 0, ivMaisEncrypted, iv.length, encrypted.length);

        // Retorna em Base64 para facilitar salvar no banco ou JSON
        return Base64.getEncoder().encodeToString(ivMaisEncrypted);
    }

    public String decrypt(String encryptedText) throws Exception {
        byte[] ivMaisEncrypted = Base64.getDecoder().decode(encryptedText);

        // Extrai o IV dos primeiros 16 bytes
        byte[] iv = new byte[16];
        byte[] encrypted = new byte[ivMaisEncrypted.length - 16];

        System.arraycopy(ivMaisEncrypted, 0, iv, 0, 16);
        System.arraycopy(ivMaisEncrypted, 16, encrypted, 0, encrypted.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Descriptografa
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decrypted = cipher.doFinal(encrypted);

        return new String(decrypted);
    }
}
