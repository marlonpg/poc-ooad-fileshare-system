package com.example.fileshare.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class FileVersion {
    private String id;
    private String fileId;
    private int version;
    private String encryptedPath;
    private String keyId;
    private String iv;
    private String checksum;
    private LocalDateTime createdAt;

    public FileVersion() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    public FileVersion(String fileId, int version, String encryptedPath, String keyId, String iv, String checksum) {
        this();
        this.fileId = fileId;
        this.version = version;
        this.encryptedPath = encryptedPath;
        this.keyId = keyId;
        this.iv = iv;
        this.checksum = checksum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getEncryptedPath() {
        return encryptedPath;
    }

    public void setEncryptedPath(String encryptedPath) {
        this.encryptedPath = encryptedPath;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "FileVersion{" +
                "id='" + id + '\'' +
                ", fileId='" + fileId + '\'' +
                ", version=" + version +
                ", keyId='" + keyId + '\'' +
                '}';
    }
}
