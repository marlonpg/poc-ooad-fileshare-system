package com.example.fileshare.service;

import com.example.fileshare.service.model.EncryptionResult;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AesGcmEncryptionService implements EncryptionService {
    private static final String ALGORITHM = "AES";
    private static final String CIPHER_INSTANCE = "AES/GCM/NoPadding";
    private static final int KEY_SIZE = 256;
    private static final int IV_SIZE = 12;
    private static final int TAG_SIZE = 128;
    private static final int BUFFER_SIZE = 8192;

    private final Map<String, SecretKey> keyStore;
    private final SecureRandom secureRandom;

    public AesGcmEncryptionService() {
        this.keyStore = new HashMap<>();
        this.secureRandom = new SecureRandom();
    }

    @Override
    public String generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_SIZE, secureRandom);
            SecretKey key = keyGen.generateKey();

            String keyId = UUID.randomUUID().toString();
            keyStore.put(keyId, key);

            return keyId;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate encryption key", e);
        }
    }

    @Override
    public EncryptionResult encrypt(String keyId, InputStream input, OutputStream output) throws EncryptionException {
        SecretKey key = retrieveKey(keyId);

        try {
            // IV is a random, non-secret value that makes each encryption unique.
            byte[] iv = new byte[IV_SIZE];
            secureRandom.nextBytes(iv);

            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_SIZE, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);
            // Write IV at the beginning of the output stream.
            output.write(iv);
            output.flush();

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                //encrypt file by chunks
                byte[] encrypted = cipher.update(buffer, 0, bytesRead);
                if (encrypted != null) {
                    output.write(encrypted);
                }
            }
            //finishes and appends the auth tag.
            byte[] finalBlock = cipher.doFinal();
            output.write(finalBlock);
            output.flush();

            String ivBase64 = Base64.getEncoder().encodeToString(iv);
            return new EncryptionResult(keyId, ivBase64);
        } catch (Exception e) {
            throw new EncryptionException("AES-GCM encryption failed", e);
        }
    }

    @Override
    public void decrypt(String keyId, String ivBase64, InputStream input, OutputStream output) throws EncryptionException {
        SecretKey key = retrieveKey(keyId);

        try {
            byte[] iv = Base64.getDecoder().decode(ivBase64);
            if (iv.length != IV_SIZE) {
                throw new EncryptionException("Invalid IV size: expected " + IV_SIZE + ", got " + iv.length);
            }

            Cipher cipher = Cipher.getInstance(CIPHER_INSTANCE);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_SIZE, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                byte[] decrypted = cipher.update(buffer, 0, bytesRead);
                if (decrypted != null) {
                    output.write(decrypted);
                }
            }

            byte[] finalBlock = cipher.doFinal();
            output.write(finalBlock);
            output.flush();
        } catch (EncryptionException e) {
            throw e;
        } catch (Exception e) {
            throw new EncryptionException("AES-GCM decryption failed", e);
        }
    }

    @Override
    public String rotateKey(String oldKeyId) throws EncryptionException {
        if (!keyStore.containsKey(oldKeyId)) {
            throw new EncryptionException("Key not found: " + oldKeyId);
        }
        return generateKey();
    }

    private SecretKey retrieveKey(String keyId) throws EncryptionException {
        SecretKey key = keyStore.get(keyId);
        if (key == null) {
            throw new EncryptionException("Key not found: " + keyId);
        }
        return key;
    }
}
