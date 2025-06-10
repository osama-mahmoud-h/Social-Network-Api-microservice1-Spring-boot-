package com.app.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FileResponseDto {
    private Long fileId;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String fileName;
    private String fileExtension;

    //f.fileId, f.fileName, f.fileUrl, f.fileType, f.fileSizeInBytes, f.fileExtension
//    public FileResponseDto(Long fileId, String fileName, String fileUrl, String fileType, Long fileSize, String fileExtension){
//        this.fileId = fileId;
//        this.fileName = fileName;
//        this.fileUrl = fileUrl;
//        this.fileType = fileType;
//        this.fileSize = fileSize;
//        this.fileExtension = fileExtension;
//    }
}
