package com.example.fileshare.domain;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AccessPermission {
    private String id;
    private String fileId;
    private Map<String, String> grants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AccessPermission() {
        this.id = UUID.randomUUID().toString();
        this.grants = new HashMap<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public AccessPermission(String fileId) {
        this();
        this.fileId = fileId;
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

    public Map<String, String> getGrants() {
        return grants;
    }

    public void setGrants(Map<String, String> grants) {
        this.grants = grants;
        this.updatedAt = LocalDateTime.now();
    }

    public void grantAccess(String userId, String permission) {
        this.grants.put(userId, permission);
        this.updatedAt = LocalDateTime.now();
    }

    public void revokeAccess(String userId) {
        this.grants.remove(userId);
        this.updatedAt = LocalDateTime.now();
    }

    public boolean hasAccess(String userId) {
        return this.grants.containsKey(userId);
    }

    public String getPermission(String userId) {
        return this.grants.get(userId);
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
        return "AccessPermission{" +
                "id='" + id + '\'' +
                ", fileId='" + fileId + '\'' +
                ", grantCount=" + grants.size() +
                '}';
    }
}
