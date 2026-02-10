package com.example.fileshare.service;

import com.example.fileshare.service.model.EncryptionResult;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EncryptionServiceImplTest {

    @Test
    void encryptThenDecrypt_roundTrip() throws Exception {
        EncryptionServiceImpl service = new EncryptionServiceImpl();
        String keyId = service.generateKey();
        byte[] plaintext = "hello-fileshare".getBytes(StandardCharsets.UTF_8);

        ByteArrayOutputStream encryptedOut = new ByteArrayOutputStream();
        EncryptionResult result = service.encrypt(
                keyId,
                new ByteArrayInputStream(plaintext),
                encryptedOut
        );

        byte[] encryptedBytes = encryptedOut.toByteArray();
        byte[] ivFromOutput = new byte[12];
        System.arraycopy(encryptedBytes, 0, ivFromOutput, 0, 12);

        assertEquals(result.getIvBase64(), Base64.getEncoder().encodeToString(ivFromOutput));

        ByteArrayInputStream cipherInput = new ByteArrayInputStream(encryptedBytes, 12, encryptedBytes.length - 12);
        ByteArrayOutputStream decryptedOut = new ByteArrayOutputStream();

        service.decrypt(keyId, result.getIvBase64(), cipherInput, decryptedOut);

        assertArrayEquals(plaintext, decryptedOut.toByteArray());
    }

    @Test
    void decrypt_withTamperedCiphertext_throws() throws Exception {
        EncryptionServiceImpl service = new EncryptionServiceImpl();
        String keyId = service.generateKey();
        byte[] plaintext = "tamper-check".getBytes(StandardCharsets.UTF_8);

        ByteArrayOutputStream encryptedOut = new ByteArrayOutputStream();
        EncryptionResult result = service.encrypt(
                keyId,
                new ByteArrayInputStream(plaintext),
                encryptedOut
        );

        byte[] encryptedBytes = encryptedOut.toByteArray();
        encryptedBytes[encryptedBytes.length - 1] ^= 0x01;

        ByteArrayInputStream cipherInput = new ByteArrayInputStream(encryptedBytes, 12, encryptedBytes.length - 12);
        ByteArrayOutputStream decryptedOut = new ByteArrayOutputStream();

        assertThrows(EncryptionException.class, () ->
                service.decrypt(keyId, result.getIvBase64(), cipherInput, decryptedOut)
        );
    }

    @Test
    void decrypt_withInvalidIv_throws() throws Exception {
        EncryptionServiceImpl service = new EncryptionServiceImpl();
        String keyId = service.generateKey();

        byte[] bogusCipher = "cipher".getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream cipherInput = new ByteArrayInputStream(bogusCipher);
        ByteArrayOutputStream decryptedOut = new ByteArrayOutputStream();

        assertThrows(EncryptionException.class, () ->
                service.decrypt(keyId, "invalid-iv", cipherInput, decryptedOut)
        );
    }
}
