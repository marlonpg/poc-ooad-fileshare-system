package com.example.fileshare.controller;

import com.example.fileshare.domain.File;
import com.example.fileshare.service.FileService;
import com.example.fileshare.service.model.FileRequest;
import com.example.fileshare.service.model.FileResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    public FileResponse uploadFile(@RequestParam("ownerId") String ownerId,
                                   @RequestParam("file") MultipartFile file) {
        try {
            FileRequest request = new FileRequest(
                    file.getOriginalFilename(),
                    ownerId,
                    file.getInputStream(),
                    file.getSize()
            );
            return fileService.saveFile(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileId,
                                               @RequestParam("userId") String userId) {
        try (InputStream input = fileService.retrieveFile(fileId, userId)) {
            byte[] data = input.readAllBytes();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(data);
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file", e);
        }
    }

    @PutMapping("/{fileId}")
    public FileResponse updateFile(@PathVariable String fileId,
                                   @RequestParam("userId") String userId,
                                   @RequestParam("file") MultipartFile file) {
        try {
            FileRequest request = new FileRequest(
                    file.getOriginalFilename(),
                    userId,
                    file.getInputStream(),
                    file.getSize()
            );
            return fileService.updateFile(fileId, userId, request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update file", e);
        }
    }

    @DeleteMapping("/{fileId}")
    public FileResponse deleteFile(@PathVariable String fileId,
                                   @RequestParam("userId") String userId) {
        return fileService.deleteFile(fileId, userId);
    }

    @GetMapping
    public List<File> listFiles(@RequestParam("userId") String userId) {
        return fileService.listFiles(userId);
    }

    @GetMapping("/search")
    public List<String> searchFiles(@RequestParam("query") String query) {
        return fileService.searchFiles(query);
    }
}
