package com.app.server.utils.file;

import lombok.Getter;
import org.springframework.core.io.InputStreamResource;

import java.io.InputStream;

@Getter
public class  PartialInputStreamResource extends InputStreamResource {
    private final long contentLength;
    private final String filename;

    public PartialInputStreamResource(InputStream inputStream, String filename, long contentLength) {
        super(inputStream);
        this.filename = filename;
        this.contentLength = contentLength;
    }
}