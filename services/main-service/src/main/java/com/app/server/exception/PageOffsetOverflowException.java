package com.app.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PageOffsetOverflowException extends RuntimeException {

    public PageOffsetOverflowException(long page, long size) {
        super("Page offset (page × size = " + page + " × " + size + ") exceeds the maximum allowed value");
    }
}