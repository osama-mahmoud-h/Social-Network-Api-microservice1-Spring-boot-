package com.app.server.strategy.fileStorageService;

import com.app.server.enums.FileStorageServiceType;
import com.app.server.utils.fileStorage.FilesStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileStorageServiceRegistry {
    private final Map<FileStorageServiceType, FilesStorageService> storageServiceMap;

    public FileStorageServiceRegistry(List<FilesStorageService> filesStorageServiceList){
        this.storageServiceMap = filesStorageServiceList.stream().collect(
                Collectors.toMap(
                        FilesStorageService::getProvider,
                        Function.identity()
                )
        );
        log.info("Registered {} FileStorageServiceRegistry attribute extractors: {}",
                storageServiceMap.size(),
                storageServiceMap.keySet());
    }


    public FilesStorageService getProvider(FileStorageServiceType provider) {
        FilesStorageService extractor = storageServiceMap.get(provider);
        if (extractor == null) {
            log.error("No FilesStorageService attribute extractor found for provider: {}", provider);
            throw new UnsupportedOperationException("Unsupported FilesStorageService provider: " + provider);
        }
        log.debug("Retrieved FilesStorageService for provider: {}", provider);
        return extractor;
    }
}
