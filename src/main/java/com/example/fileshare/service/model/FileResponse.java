package com.example.fileshare.service.model;

import java.time.LocalDateTime;

public class FileResponse {
    private String fileId;
    private String fileName;
    private String ownerId;
    private long fileSize;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FileResponse(String fileId, String fileName, String ownerId, long fileSize, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.ownerId = ownerId;
        this.fileSize = fileSize;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
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

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "FileResponse{" +
                "fileId='" + fileId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", status='" + status + '\'' +
                "}';
    }
}
