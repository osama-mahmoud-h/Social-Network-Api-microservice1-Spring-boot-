package com.app.server.controller;

import com.app.server.dto.request.file.GetFileRequestDto;
import com.app.server.service.FileService;
import com.app.shared.security.dto.MyApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Files", description = "APIs for secure file access and streaming")
@SecurityRequirement(name = "jwtAuth")
@Slf4j
@RequiredArgsConstructor
public class FilesController {
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
    @Operation(
        summary = "Get file with security and streaming support",
        description = "Download or stream a file using query parameters. Supports HTTP Range requests for large files. " +
                     "Validates user has access to the file. " +
                     "Example: GET /api/v1/files?filename=posts/file.jpg&inline=true"
    )
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Resource> getFile(
            @Valid @ModelAttribute GetFileRequestDto getFileRequestDto,

            @Parameter(
                description = "HTTP Range header for partial content streaming. " +
                             "Format: 'bytes=start-end'. Examples: 'bytes=0-1023' (first 1KB), " +
                             "'bytes=1000-' (from byte 1000 to end), 'bytes=-500' (last 500 bytes). " +
                             "Enables video seeking, resume downloads, and progressive loading.",
                example = "bytes=0-1023",
                required = false
            )
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
    @GetMapping("/url")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<MyApiResponse<String>> getContentUrl(
            @Parameter(description = "File name to get secure URL for") @RequestParam String fileName
    ) {
        String getFileAccessUrl = fileService.getFileUrl(fileName);
        return ResponseEntity.ok(
            MyApiResponse.success("Secure file URL generated successfully", getFileAccessUrl)
        );
    }

}