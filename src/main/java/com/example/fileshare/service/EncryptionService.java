package com.example.fileshare.service;

import com.example.fileshare.service.model.EncryptionResult;

import java.io.InputStream;
import java.io.OutputStream;

public interface EncryptionService {
    String generateKey();

    EncryptionResult encrypt(String keyId, InputStream input, OutputStream output) throws EncryptionException;

    void decrypt(String keyId, String ivBase64, InputStream input, OutputStream output) throws EncryptionException;

    String rotateKey(String oldKeyId) throws EncryptionException;
}
