package com.app.server.service;


import com.app.server.dto.request.file.GetFileRequestDto;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

public interface FileService {

    ResponseEntity<Resource> getFile(GetFileRequestDto request, String rangeHeader);

    String getFileUrl(String fileName);
}