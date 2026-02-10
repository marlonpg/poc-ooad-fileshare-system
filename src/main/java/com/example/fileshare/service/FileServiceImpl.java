package com.example.fileshare.service;

import com.example.fileshare.domain.File;
import com.example.fileshare.domain.FileVersion;
import com.example.fileshare.service.model.EncryptionResult;
import com.example.fileshare.service.model.FileRequest;
import com.example.fileshare.service.model.FileResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;


public class FileServiceImpl implements FileService {

    private static final int IV_SIZE = 12;
    
    private final EncryptionService encryptionService;
    private final Map<String, File> fileStore;
    private final Map<String, List<FileVersion>> versionStore;
    private final Map<String, byte[]> encryptedDataStore;
    private final Map<String, Set<String>> searchIndex;
    
    public FileServiceImpl() {
        this.encryptionService = new AesGcmEncryptionService();
        this.fileStore = new HashMap<>();
        this.versionStore = new HashMap<>();
        this.encryptedDataStore = new HashMap<>();
        this.searchIndex = new HashMap<>();
    }

    @Override
    public FileResponse saveFile(FileRequest request) {
        try {
            byte[] fileData = readInputStream(request.getFileData());
            String checksum = calculateChecksum(fileData);
            String keyId = encryptionService.generateKey();

            File file = new File(
                    request.getOwnerId(),
                    request.getFileName(),
                    fileData.length,
                    checksum
            );
            String fileId = file.getId();

            ByteArrayOutputStream encryptedBuffer = new ByteArrayOutputStream();
            EncryptionResult encryptionResult = encryptionService.encrypt(
                keyId,
                new ByteArrayInputStream(fileData),
                encryptedBuffer
            );
            byte[] encryptedData = encryptedBuffer.toByteArray();
            String storageKey = "enc-" + fileId;
            encryptedDataStore.put(storageKey, encryptedData);

            FileVersion version = new FileVersion(
                    fileId,
                    1,
                storageKey,
                keyId,
                encryptionResult.getIvBase64(),
                    checksum
            );
            
            fileStore.put(fileId, file);
            versionStore.put(fileId, new ArrayList<>(Arrays.asList(version)));
            
            indexFile(fileId, request.getFileName(), request.getOwnerId());
            
            return new FileResponse(
                    fileId,
                    file.getName(),
                    file.getOwnerId(),
                    file.getSize(),
                    file.getStatus().toString(),
                    file.getCreatedAt(),
                    file.getUpdatedAt()
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

    @Override
    public InputStream retrieveFile(String fileId, String userId) {
        try {
            File file = fileStore.get(fileId);
            if (file == null) {
                throw new IllegalArgumentException("File not found: " + fileId);
            }
            
            if (!file.getOwnerId().equals(userId)) {
                throw new IllegalAccessException("Unauthorized: user " + userId + " has no access to " + fileId);
            }
            
            if (file.getStatus() == File.Status.DELETED) {
                throw new IllegalArgumentException("File is deleted: " + fileId);
            }
            
            List<FileVersion> versions = versionStore.get(fileId);
            if (versions == null || versions.isEmpty()) {
                throw new IllegalArgumentException("No versions found for file: " + fileId);
            }
            FileVersion latestVersion = versions.get(versions.size() - 1);
            
            byte[] encryptedData = encryptedDataStore.get(latestVersion.getEncryptedPath());
            if (encryptedData == null) {
                throw new IllegalArgumentException("Encrypted data not found for file: " + fileId);
            }
            if (encryptedData.length <= IV_SIZE) {
                throw new IllegalArgumentException("Encrypted data is too short for file: " + fileId);
            }

            ByteArrayInputStream cipherInput = new ByteArrayInputStream(
                    encryptedData,
                    IV_SIZE,
                    encryptedData.length - IV_SIZE
            );
            ByteArrayOutputStream decryptedBuffer = new ByteArrayOutputStream();
            encryptionService.decrypt(
                    latestVersion.getKeyId(),
                    latestVersion.getIv(),
                    cipherInput,
                    decryptedBuffer
            );

            return new ByteArrayInputStream(decryptedBuffer.toByteArray());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve file", e);
        }
    }

    @Override
    public FileResponse deleteFile(String fileId, String userId) {
        try {
            File file = fileStore.get(fileId);
            if (file == null) {
                throw new IllegalArgumentException("File not found: " + fileId);
            }
            
            if (!file.getOwnerId().equals(userId)) {
                throw new IllegalAccessException("Unauthorized: user " + userId);
            }
            
            file.setStatus(File.Status.DELETED);
            
            List<FileVersion> versions = versionStore.get(fileId);
            if (versions != null) {
                for (FileVersion version : versions) {
                    encryptedDataStore.remove(version.getEncryptedPath());
                }
            }
            searchIndex.remove(fileId);
            
            return new FileResponse(
                    fileId,
                    file.getName(),
                    file.getOwnerId(),
                    file.getSize(),
                    file.getStatus().toString(),
                    file.getCreatedAt(),
                    file.getUpdatedAt()
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    @Override
    public List<File> listFiles(String userId) {
        List<File> userFiles = new ArrayList<>();
        
        for (File file : fileStore.values()) {
            if (file.getOwnerId().equals(userId) && file.getStatus() != File.Status.DELETED) {
                userFiles.add(file);
            }
        }
        
        return userFiles;
    }
    
    @Override
    public List<String> searchFiles(String query) {
        List<String> results = new ArrayList<>();
        String queryLower = query.toLowerCase();
        
        for (Map.Entry<String, Set<String>> entry : searchIndex.entrySet()) {
            String fileId = entry.getKey();
            Set<String> tokens = entry.getValue();
            
            // Check if any token matches/contains query
            for (String token : tokens) {
                if (token.contains(queryLower)) {
                    results.add(fileId);
                    break;
                }
            }
        }
        
        return results;
    }
    
    private void indexFile(String fileId, String fileName, String ownerId) {
        Set<String> tokens = new HashSet<>();
        

        String[] parts = fileName.toLowerCase().split("[\\s._-]+");
        for (String part : parts) {
            if (!part.isEmpty()) {
                tokens.add(part);
            }
        }

        tokens.add(ownerId);
        
        searchIndex.put(fileId, tokens);
    }
    
    private String calculateChecksum(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        return Base64.getEncoder().encodeToString(hash);
    }

    private byte[] readInputStream(InputStream input) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int nRead;
        
        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        
        return buffer.toByteArray();
    }
}
