package com.app.server.utils.fileStorage;

import com.app.server.enums.FileStorageServiceType;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface FilesStorageService {

    public FileStorageServiceType getProvider();

    public void init();

    public String save(MultipartFile file);

    public Resource load(String filename);

    public void deleteAll();

    boolean deleteFile(String filename);

    public Stream<Path> loadAll();

    String getPresignedUrl(String objectName);

    /**
     * Get public URL for file access
     * Default implementation returns the filename as-is (for local filesystem)
     * MinIO implementation overrides to return presigned URLs
     */
    default String getFileUrl(String filename) {
        return filename;
    }

    InputStream loadRange(String filename, long offset, long length);

    long getFileSize(String filename);

    boolean fileExists(String filename);
}
