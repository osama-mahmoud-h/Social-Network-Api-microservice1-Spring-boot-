package com.app.server.mapper;


import com.app.server.dto.response.FileResponseDto;
import com.app.server.model.File;
import com.app.server.utils.fileStorage.FileUtils;
import com.app.server.utils.fileStorage.FilesStorageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class FileMapper {
    private final FilesStorageService filesStorageService;
    private final FileUtils fileUtils;

    public File mapMultiPartFileToFileSchema(MultipartFile multipartFile) throws RuntimeException{
        String newSavedFileName = this.filesStorageService.save(multipartFile);
        return File.builder()
                .fileName(multipartFile.getOriginalFilename())
                .fileExtension(this.fileUtils.extractExtension(multipartFile.getOriginalFilename()))
                .fileUrl(newSavedFileName)
                .fileSizeInBytes(multipartFile.getSize())
                .fileType(multipartFile.getContentType())
                .build();
    }

    public FileResponseDto mapFileToFileResponseDto(File file){
        return FileResponseDto.builder()
                .fileId(file.getFileId())
                .fileUrl(file.getFileUrl())
                .fileType(file.getFileType())
                .fileSize(file.getFileSizeInBytes())
                .fileName(file.getFileName())
                .fileExtension(file.getFileExtension())
                .build();
    }
}
