package com.app.server.strategy.fileStorageService;

import com.app.server.enums.FileStorageServiceType;
import com.app.server.utils.fileStorage.FilesStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileStorageServiceFactory {
    private final FileStorageServiceRegistry registry;

    // CREATES/PROVIDES instances (may add logic)
    public FilesStorageService createStorageService(FileStorageServiceType type) {
        return registry.getProvider(type);
    }

    // Factory can CREATE new instances with logic
    public FilesStorageService createWithFallback(FileStorageServiceType primary,
                                                  FileStorageServiceType fallback) {
        try {
            return registry.getProvider(primary);
        } catch (Exception e) {
            return registry.getProvider(fallback);
        }
    }

    // Convenience methods for common types
    public FilesStorageService getAwsS3Service() {
        return createStorageService(FileStorageServiceType.AWS_S3);
    }

    public FilesStorageService getMinioService() {
        return createStorageService(FileStorageServiceType.MINIO);
    }

    public FilesStorageService getFileSystemService() {
        return createStorageService(FileStorageServiceType.FILE_SYSTEM);
    }
}
