package com.example.fileshare.service.model;

public class EncryptionResult {
    private String keyId;
    private String ivBase64;

    public EncryptionResult(String keyId, String ivBase64) {
        this.keyId = keyId;
        this.ivBase64 = ivBase64;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getIvBase64() {
        return ivBase64;
    }

    public void setIvBase64(String ivBase64) {
        this.ivBase64 = ivBase64;
    }
}
