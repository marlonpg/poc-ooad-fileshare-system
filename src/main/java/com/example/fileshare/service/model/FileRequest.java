package com.example.fileshare.service.model;

import java.io.InputStream;

public class FileRequest {
    private String fileName;
    private String ownerId;
    private InputStream fileData;
    private long fileSize;

    public FileRequest(String fileName, String ownerId, InputStream fileData, long fileSize) {
        this.fileName = fileName;
        this.ownerId = ownerId;
        this.fileData = fileData;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public InputStream getFileData() {
        return fileData;
    }

    public void setFileData(InputStream fileData) {
        this.fileData = fileData;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
