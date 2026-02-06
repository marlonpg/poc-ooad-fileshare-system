package com.example.fileshare.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SearchIndexEntry {
    private String id;
    private String fileId;
    private Set<String> tokens; // tokenized name and metadata
    private Set<String> tags;
    private LocalDateTime indexedAt;

    public SearchIndexEntry() {
        this.id = UUID.randomUUID().toString();
        this.tokens = new HashSet<>();
        this.tags = new HashSet<>();
        this.indexedAt = LocalDateTime.now();
    }

    public SearchIndexEntry(String fileId) {
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

    public Set<String> getTokens() {
        return tokens;
    }

    public void setTokens(Set<String> tokens) {
        this.tokens = tokens;
    }

    public void addToken(String token) {
        this.tokens.add(token.toLowerCase());
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public void addTag(String tag) {
        this.tags.add(tag.toLowerCase());
    }

    public LocalDateTime getIndexedAt() {
        return indexedAt;
    }

    public void setIndexedAt(LocalDateTime indexedAt) {
        this.indexedAt = indexedAt;
    }

    @Override
    public String toString() {
        return "SearchIndexEntry{" +
                "id='" + id + '\'' +
                ", fileId='" + fileId + '\'' +
                ", tokenCount=" + tokens.size() +
                ", tagCount=" + tags.size() +
                '}';
    }
}
