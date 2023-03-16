package com.example.server.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FilesStorageService {
    public void init();

    public void save(MultipartFile file,String filename);

    public Resource load(String filename);

    public void deleteAll();

    public Stream<Path> loadAll();
}
