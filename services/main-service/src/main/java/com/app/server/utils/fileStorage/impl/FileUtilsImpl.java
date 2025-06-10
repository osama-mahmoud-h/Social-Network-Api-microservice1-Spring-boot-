package com.app.server.utils.fileStorage.impl;

import com.app.server.exception.CustomRuntimeException;
import com.app.server.utils.fileStorage.FileUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class FileUtilsImpl implements FileUtils {

    @Override
    public String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new CustomRuntimeException("Original filename must not be null or empty", HttpStatus.BAD_REQUEST);
            }
            String extension = extractExtension(originalFilename);
            return UUID.randomUUID() + extension;
    }

    @Override
    public boolean isValidImageType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String mimeType = file.getContentType();
        return mimeType != null && (
                mimeType.equals("image/jpeg") ||
                        mimeType.equals("image/png") ||
                        mimeType.equals("image/gif") ||
                        mimeType.equals("image/bmp"));
    }

//    @Override
//    public Str

    @Override
    public String extractExtension(String filename) {
        int extensionIndex = filename.lastIndexOf(".");
        if (extensionIndex != -1 && extensionIndex < filename.length() - 1) {
            return filename.substring(extensionIndex);
        }
        return "";
    }

    @Override
    public String extractType(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "";
        }
        String mimeType = file.getContentType();
        if (mimeType != null) {
            return mimeType;
        }
        return "";
    }

    //file size
    @Override
    public long getFileSize(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return 0L;
        }
        return file.getSize();
    }
}
