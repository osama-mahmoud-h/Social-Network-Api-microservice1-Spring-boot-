package com.app.server.utils.fileStorage.impl;


import com.app.server.enums.FileStorageServiceType;
import com.app.server.exception.CustomRuntimeException;
import com.app.server.utils.fileStorage.FileUtils;
import com.app.server.utils.fileStorage.FilesStorageService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service("FilesStorageServiceImpl")
@ConditionalOnProperty(name = "minio.enabled", havingValue = "false")
public class FilesStorageServiceImpl implements FilesStorageService {
    private static final Logger logger = LoggerFactory.getLogger(FilesStorageServiceImpl.class);
    private final FileUtils fileUtils;

    @Value("${file.upload-dir}")
    private  String uploadDir;
    private  Path root ;

    @Autowired
    public FilesStorageServiceImpl(FileUtils fileUtils) {
        this.fileUtils = fileUtils;
    }

    @Override
    public FileStorageServiceType getProvider() {
        return FileStorageServiceType.FILE_SYSTEM;
    }

    @Override
    @PostConstruct
    public void init() {
        try {
            this.root = Paths.get(uploadDir);
            Files.createDirectories(root);
            logger.info("Upload directory created at: {}", root.toString());
        } catch (IOException e) {
            logger.error("Could not initialize folder for upload!", e);
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public String save(MultipartFile file) {
        try {
            String newFileName = fileUtils.generateFileName(file);
            Files.copy(file.getInputStream(), this.root.resolve(newFileName));
            return newFileName;
        } catch (Exception e) {
            logger.error("error occurred while saving the file", e);
            if (e instanceof FileAlreadyExistsException) {
                throw new CustomRuntimeException("A file of that name already exists.", HttpStatus.BAD_REQUEST);
            }
            throw new CustomRuntimeException("error occurred while saving the file", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public boolean deleteFile(String filename) {
        try {
            Files.deleteIfExists(root.resolve(filename));
            return true;
        } catch (IOException e) {
            throw new CustomRuntimeException("Could not delete the file!", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1)
                    .filter(path -> !path.equals(this.root))
                    .map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    @Override
    public String getPresignedUrl(String objectName) {
        return "";
    }

    @Override
    public InputStream loadRange(String filename, long offset, long length) {
        return null;
    }

    @Override
    public long getFileSize(String filename) {
        return 0;
    }

    @Override
    public boolean fileExists(String filename) {
        return false;
    }
}
