package com.app.server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyApiResponse<T> {
    private int status;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private String message;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private T data;



    public static <T> MyApiResponse<T> success(T data, String message) {
        return createResponse(data, HttpStatus.OK, message);
    }

    public static <T> MyApiResponse<T> success(T data, HttpStatus httpStatus, String message) {
        return createResponse(data, httpStatus, message);
    }

    private static <T> MyApiResponse<T> createResponse(T data, HttpStatus httpStatus, String message) {
        return MyApiResponse.<T>builder()
                .status(httpStatus.value())
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}