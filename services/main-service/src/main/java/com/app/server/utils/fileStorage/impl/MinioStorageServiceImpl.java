package com.app.server.utils.fileStorage.impl;

import com.app.server.config.MinioProperties;
import com.app.server.enums.FileCategory;
import com.app.server.enums.FileStorageServiceType;
import com.app.server.exception.CustomRuntimeException;
import com.app.server.utils.fileStorage.FileUtils;
import com.app.server.utils.fileStorage.FilesStorageService;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service("MinioStorageServiceImpl")
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "minio.enabled", havingValue = "true", matchIfMissing = true)
public class MinioStorageServiceImpl implements FilesStorageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;
    private final FileUtils fileUtils;

    @Override
    public FileStorageServiceType getProvider() {
        return FileStorageServiceType.MINIO;
    }

    @Override
    @PostConstruct
    public void init() {
        log.info("MinIO Storage Service initialized with bucket: {}",
                minioProperties.getBucketName());
    }

    @Override
    public String save(MultipartFile file) {
        return save(file, FileCategory.POST_ATTACHMENT);
    }

    public String save(MultipartFile file, FileCategory category) {
        try {
            String fileName = fileUtils.generateFileName(file);
            String objectName = category.getFolderPrefix() + fileName;

            log.debug("Uploading file to MinIO: {}", objectName);

            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );

            log.info("File uploaded successfully to MinIO: {}", objectName);
            return objectName;

        } catch (Exception e) {
            log.error("Error uploading file to MinIO: {}", e.getMessage(), e);
            throw new CustomRuntimeException(
                "Failed to upload file to object storage",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            log.debug("Loading file from MinIO: {}", filename);

            InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(filename)
                    .build()
            );

            return new InputStreamResource(stream);

        } catch (Exception e) {
            log.error("Error loading file from MinIO: {}", e.getMessage(), e);
            throw new CustomRuntimeException(
                "Could not read the file from object storage",
                HttpStatus.NOT_FOUND
            );
        }
    }

    @Override
    public boolean deleteFile(String filename) {
        try {
            log.debug("Deleting file from MinIO: {}", filename);

            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(filename)
                    .build()
            );

            log.info("File deleted successfully from MinIO: {}", filename);
            return true;

        } catch (Exception e) {
            log.error("Error deleting file from MinIO: {}", e.getMessage(), e);
            throw new CustomRuntimeException(
                "Failed to delete file from object storage",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Override
    public void deleteAll() {
        log.warn("deleteAll() called - This operation is not supported in MinIO implementation");
        throw new UnsupportedOperationException(
            "Bulk delete is not supported. Use deleteFile() for individual files."
        );
    }

    @Override
    public Stream<Path> loadAll() {
        log.warn("loadAll() called - This operation is not supported in MinIO implementation");
        throw new UnsupportedOperationException(
            "loadAll() is not supported in MinIO implementation. Use list operations if needed."
        );
    }

    /**
     * Generate a presigned URL for direct file access
     * Useful for generating shareable URLs with expiration
     */
    @Override
    public String getPresignedUrl(String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(minioProperties.getBucketName())
                    .object(objectName)
                    .expiry(minioProperties.getUrlExpiration(), TimeUnit.SECONDS)
                    .build()
            );
        } catch (Exception e) {
            log.error("Error generating presigned URL: {}", e.getMessage(), e);
            throw new CustomRuntimeException(
                "Failed to generate file URL",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Get file URL - returns presigned URL for MinIO
     */
    @Override
    public String getFileUrl(String filename) {
        return getPresignedUrl(filename);
    }

    /**
     * Load partial file content using MinIO's native range support.
     * More efficient than loading full file and skipping bytes.
     *
     * @param filename The object name
     * @param offset Starting byte position
     * @param length Number of bytes to read
     * @return InputStream containing requested bytes
     */

    @Override
    public InputStream loadRange(String filename, long offset, long length) {
        try {
            log.info("Loading file range from MinIO: {} (offset={}, length={})",
                filename, offset, length);

            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(filename)
                    .offset(offset)
                    .length(length)
                    .build()
            );

        } catch (Exception e) {
            log.error("Error loading file range from MinIO: {}", e.getMessage(), e);
            throw new CustomRuntimeException(
                "Could not read file range from object storage",
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Get file size without downloading the file.
     * Uses HEAD request to MinIO.
     *
     * @param filename The object name
     * @return File size in bytes
     */
    @Override
    public long getFileSize(String filename) {
        try {
            var stat = minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(filename)
                    .build()
            );

            return stat.size();

        } catch (Exception e) {
            log.error("Error getting file size from MinIO: {}", e.getMessage(), e);
            throw new CustomRuntimeException(
                "Could not get file metadata",
                HttpStatus.NOT_FOUND
            );
        }
    }

    /**
     * Check if file exists in MinIO without downloading it.
     *
     * @param filename The object name
     * @return true if file exists
     */
    @Override
    public boolean fileExists(String filename) {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(filename)
                    .build()
            );
            return true;

        } catch (Exception e) {
            log.debug("File does not exist in MinIO: {}", filename);
            return false;
        }
    }
}