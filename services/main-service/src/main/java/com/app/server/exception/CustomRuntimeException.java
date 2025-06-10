package com.app.server.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;


@Data
public class CustomRuntimeException extends  RuntimeException{

    private final HttpStatus status;

    public CustomRuntimeException(String message) {
        super(message);
        this.status = HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public CustomRuntimeException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
