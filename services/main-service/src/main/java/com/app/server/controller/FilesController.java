package com.app.server.controller;

import com.app.server.controller.swagger.IFilesApi;
import com.app.server.dto.request.file.GetFileRequestDto;
import com.app.server.service.FileService;
import com.app.shared.security.dto.MyApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/files")
@Slf4j
@RequiredArgsConstructor
public class FilesController implements IFilesApi {
    private final FileService fileService;

    /**
     * Download or stream a file with security checks and range request support.
     * Supports HTTP Range requests for large file pagination/streaming.
     *
     * Uses query parameters instead of path variables to avoid URL encoding issues.
     * Example: GET /api/v1/files?filename=posts/file.jpg&inline=true
     *
     * @param getFileRequestDto File request parameters (filename, inline)
     * @param rangeHeader HTTP Range header for partial content requests (e.g., "bytes=0-1023")
     * @return File content with appropriate headers
     */
    @Override
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Resource> getFile(
            @Valid @ModelAttribute GetFileRequestDto getFileRequestDto,
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String rangeHeader
    ) {
        ResponseEntity<Resource> fileResource = fileService.getFile(getFileRequestDto, rangeHeader);
        return fileResource;
    }

    /**
     * Get a secure, server-proxied file URL that requires authentication.
     * Returns a URL that goes through the server's security layer.
     *
     * @param fileName The file to get URL for
     * @return Secure URL that requires authentication
     */
    @Override
    @GetMapping("/url")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<String>> getContentUrl(
            @RequestParam String fileName
    ) {
        String getFileAccessUrl = fileService.getFileUrl(fileName);
        return ResponseEntity.ok(
            MyApiResponse.success("Secure file URL generated successfully", getFileAccessUrl)
        );
    }

}