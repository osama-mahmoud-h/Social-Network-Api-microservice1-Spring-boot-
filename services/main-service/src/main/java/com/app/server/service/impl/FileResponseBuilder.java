package com.app.server.service.impl;

import com.app.server.utils.file.FileContentTypeResolver;
import com.app.server.utils.file.Range;
import com.app.server.utils.fileStorage.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileResponseBuilder {

    private final FileUtils fileUtils;

    public ResponseEntity<Resource> buildFullFileResponse(Resource file, long fileSize, boolean inline) {
        HttpHeaders headers = buildCommonHeaders(file, inline);
        headers.setContentLength(fileSize);
        headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");

        return ResponseEntity.ok()
                .headers(headers)
                .body(file);
    }

    public ResponseEntity<Resource> buildPartialContentResponse(Resource resource,
                                                                Range range,
                                                                long fileSize,
                                                                boolean inline) {
        HttpHeaders headers = buildCommonHeaders(resource, inline);
        headers.add(HttpHeaders.CONTENT_RANGE, range.toContentRangeHeader(fileSize));
        headers.setContentLength(range.getLength());

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .headers(headers)
                .body(resource);
    }

    public ResponseEntity<Resource> buildRangeNotSatisfiableResponse(long fileSize) {
        return ResponseEntity.status(HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE)
                .header(HttpHeaders.CONTENT_RANGE, "bytes */" + fileSize)
                .build();
    }

    private HttpHeaders buildCommonHeaders(Resource file, boolean inline) {
        HttpHeaders headers = new HttpHeaders();
        String filename = file.getFilename();

        headers.setContentType(MediaType.parseMediaType(
                fileUtils.determineContentType(filename)
        ));

        String disposition = inline ? "inline" : "attachment";
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
                String.format("%s; filename=\"%s\"", disposition, filename));

        headers.setCacheControl("private, max-age=3600");
        return headers;
    }
}