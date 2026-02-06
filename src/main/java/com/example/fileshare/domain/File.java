package com.example.fileshare.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class File {
    public enum Status {
        ACTIVE, DELETED, ARCHIVED
    }

    private String id;
    private String ownerId;
    private String name;
    private long size;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String checksum;
    private Status status;

    public File() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = Status.ACTIVE;
    }

    public File(String ownerId, String name, long size, String checksum) {
        this();
        this.ownerId = ownerId;
        this.name = name;
        this.size = size;
        this.checksum = checksum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
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

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "File{" +
                "id='" + id + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", status=" + status +
                '}';
    }
}
