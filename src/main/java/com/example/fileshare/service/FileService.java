package com.example.fileshare.service;

import com.example.fileshare.service.model.FileRequest;
import com.example.fileshare.service.model.FileResponse;

import java.io.File;
import java.io.InputStream;
import java.util.List;

//`FileService` (save, retrieve, delete, list, search)
public interface FileService {
    FileResponse saveFile(FileRequest fileRequest);
    InputStream retrieveFile(String fileId, String userId);
    FileResponse deleteFile(String fileId, String userId);
    List<File> listFiles(String userId);
    List<String> searchFiles(String query);
}
