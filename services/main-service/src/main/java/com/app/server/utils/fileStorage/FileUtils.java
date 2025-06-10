package com.app.server.utils.fileStorage;

import org.springframework.web.multipart.MultipartFile;

public interface FileUtils {
    String generateFileName(MultipartFile file);

    boolean isValidImageType(MultipartFile file);

    String extractExtension(String filename);

    String extractType(MultipartFile file);

    //file size
    long getFileSize(MultipartFile file);
}
