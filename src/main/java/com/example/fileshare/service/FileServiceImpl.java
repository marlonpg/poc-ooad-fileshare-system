package com.example.fileshare.service;

import com.example.fileshare.domain.File;
import com.example.fileshare.service.model.FileRequest;
import com.example.fileshare.service.model.FileResponse;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {

    private static final String DEFAULT_CHECKSUM = "N/A";
    private static final String STORAGE_DIR = "storage";

    private final Map<String, File> fileStore;
    private final Path storageDir;

    public FileServiceImpl() {
        this.fileStore = new HashMap<>();
        this.storageDir = Paths.get(STORAGE_DIR);
        try {
            Files.createDirectories(storageDir);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize storage directory", e);
        }
    }

    @Override
    public FileResponse saveFile(FileRequest request) {
        try (InputStream input = request.getFileData()) {
            File file = new File(
                    request.getOwnerId(),
                    request.getFileName(),
                    request.getFileSize(),
                    DEFAULT_CHECKSUM
            );
            String fileId = file.getId();

            Path filePath = storageDir.resolve(fileId);
            Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);

            fileStore.put(fileId, file);
            return toResponse(file);
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

            Path filePath = storageDir.resolve(fileId);
            if (!Files.exists(filePath)) {
                throw new IllegalArgumentException("File data not found: " + fileId);
            }

            return Files.newInputStream(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve file", e);
        }
    }

    @Override
    public FileResponse updateFile(String fileId, String userId, FileRequest request) {
        try (InputStream input = request.getFileData()) {
            File file = fileStore.get(fileId);
            if (file == null) {
                throw new IllegalArgumentException("File not found: " + fileId);
            }
            if (!file.getOwnerId().equals(userId)) {
                throw new IllegalAccessException("Unauthorized: user " + userId);
            }
            if (file.getStatus() == File.Status.DELETED) {
                throw new IllegalArgumentException("File is deleted: " + fileId);
            }

            Path filePath = storageDir.resolve(fileId);
            Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);

            file.setName(request.getFileName());
            file.setSize(request.getFileSize());
            file.setChecksum(DEFAULT_CHECKSUM);

            return toResponse(file);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update file", e);
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
            
            Path filePath = storageDir.resolve(fileId);
            Files.deleteIfExists(filePath);

            return toResponse(file);
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

        for (Map.Entry<String, File> entry : fileStore.entrySet()) {
            File file = entry.getValue();
            if (file.getStatus() != File.Status.DELETED
                    && file.getName() != null
                    && file.getName().toLowerCase().contains(queryLower)) {
                results.add(entry.getKey());
            }
        }

        return results;
    }

    private FileResponse toResponse(File file) {
        return new FileResponse(
                file.getId(),
                file.getName(),
                file.getOwnerId(),
                file.getSize(),
                file.getStatus().toString(),
                file.getCreatedAt(),
                file.getUpdatedAt()
        );
    }
}
