package com.app.server.controller.swagger;

import com.app.server.dto.request.file.GetFileRequestDto;
import com.app.shared.security.dto.MyApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Files", description = "APIs for secure file access and streaming")
@SecurityRequirement(name = "jwtAuth")
public interface IFilesApi {

    @Operation(
            summary = "Get file with security and streaming support",
            description = "Download or stream a file using query parameters. Supports HTTP Range requests for large files. " +
                    "Validates user has access to the file. " +
                    "Example: GET /api/v1/files?filename=posts/file.jpg&inline=true"
    )
    ResponseEntity<Resource> getFile(
            @Valid @ModelAttribute GetFileRequestDto getFileRequestDto,

            @Parameter(
                    description = "HTTP Range header for partial content streaming. " +
                            "Format: 'bytes=start-end'. Examples: 'bytes=0-1023' (first 1KB), " +
                            "'bytes=1000-' (from byte 1000 to end), 'bytes=-500' (last 500 bytes). " +
                            "Enables video seeking, resume downloads, and progressive loading.",
                    example = "bytes=0-1023",
                    required = false
            )
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String rangeHeader);

    ResponseEntity<MyApiResponse<String>> getContentUrl(
            @Parameter(description = "File name to get secure URL for") @RequestParam String fileName);
}