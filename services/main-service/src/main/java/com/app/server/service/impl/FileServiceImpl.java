package com.app.server.service.impl;

import com.app.server.dto.request.file.GetFileRequestDto;
import com.app.server.enums.FileStorageServiceType;
import com.app.server.exception.CustomRuntimeException;
import com.app.server.repository.FileRepository;
import com.app.server.service.FileService;
import com.app.server.strategy.fileStorageService.FileStorageServiceFactory;
import com.app.server.utils.file.LimitedInputStream;
import com.app.server.utils.file.PartialInputStreamResource;
import com.app.server.utils.file.Range;
import com.app.server.utils.file.RangeParser;
import com.app.server.utils.fileStorage.FileUtils;
import com.app.server.utils.fileStorage.FilesStorageService;
import com.app.shared.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of file access validation service.
 * Checks if users have permission to access files based on ownership and visibility rules.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;

    @Value("${app.storage.default-provider:MINIO}") // Can change to S3 in config
    private FileStorageServiceType defaultProvider;
    private final FileStorageServiceFactory fileStorageServiceFactory;
    private final FileAccessValidator fileAccessValidator;
    private final FileUtils fileUtils;
    private final FileResponseBuilder fileResponseBuilder;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Resource> getFile(GetFileRequestDto request, String rangeHeader) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        String filename = request.getFilename();
        boolean inline = request.getInline() != null ? request.getInline() : true;

        log.info("User {} accessing file: {} (inline: {})", currentUserId, filename, inline);

        validateFileAccess(currentUserId, filename);

        FilesStorageService storageService = fileStorageServiceFactory.createStorageService(defaultProvider);

        try {
            long fileSize = storageService.getFileSize(filename);

            if (isRangeRequest(rangeHeader)) {
                return handleRangeRequest(storageService, filename, rangeHeader, fileSize, inline);
            }

            return handleFullFileRequest(storageService, filename, fileSize, inline);
        } catch (Exception e) {
            log.error("Error reading file {}: {}", filename, e.getMessage(), e);
            throw new CustomRuntimeException("Error reading file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String getFileUrl(String fileName) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        validateFileAccess(currentUserId, fileName);

        String secureUrl = String.format("/api/v1/files?filename=%s&inline=true", fileName);
        log.info("User {} requested secure URL for file: {}", currentUserId, fileName);
        return secureUrl;
    }

    private void validateFileAccess(Long userId, String filename) {
        if (! fileAccessValidator.canAccessFileByObjectName(userId, filename)) {
            log.warn("User {} attempted to access unauthorized file: {}", userId, filename);
            throw new CustomRuntimeException(
                    "You don't have permission to access this file",
                    HttpStatus.FORBIDDEN
            );
        }
    }

    private boolean isRangeRequest(String rangeHeader) {
        return rangeHeader != null && rangeHeader.startsWith("bytes=");
    }

    private ResponseEntity<Resource> handleRangeRequest(FilesStorageService storageService,
                                        String filename,
                                        String rangeHeader,
                                        long fileSize,
                                        boolean inline) throws IOException {

        Range range = RangeParser.parse(rangeHeader, fileSize);

        if (!range.isValid(fileSize)) {
            return fileResponseBuilder.buildRangeNotSatisfiableResponse(fileSize);
        }

        InputStream rangeStream = storageService.loadRange(filename, range.getStart(), range.getLength());
        InputStream limitedStream = new LimitedInputStream(rangeStream, range.getLength());

        Resource partialResource = new PartialInputStreamResource(
                limitedStream,
                extractFilename(filename),
                range.getLength()
        );

        return fileResponseBuilder.buildPartialContentResponse(
                partialResource,
                range,
                fileSize,
                inline
        );
    }

    private ResponseEntity<Resource> handleFullFileRequest(FilesStorageService storageService,
                                           String filename,
                                           long fileSize,
                                           boolean inline) throws IOException {

        Resource file = storageService.load(filename);
        return fileResponseBuilder.buildFullFileResponse(file, fileSize, inline);
    }

    private String extractFilename(String path) {
        return path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;
    }

}