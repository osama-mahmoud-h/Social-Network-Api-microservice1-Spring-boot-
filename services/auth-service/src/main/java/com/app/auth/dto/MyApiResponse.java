package com.app.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response wrapper")
public class MyApiResponse<T> {

    @Schema(description = "Indicates if the request was successful", example = "true")
    private boolean success;

    @Schema(description = "Response message", example = "Operation completed successfully")
    private String message;

    @Schema(description = "Response data")
    private T data;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Timestamp of the response")
    private LocalDateTime timestamp;

    @Schema(description = "Error details (only present on failure)")
    private ErrorDetails error;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetails {
        private String code;
        private String details;
        private Object validationErrors;
    }

    // Success response builders
    public static <T> MyApiResponse<T> success(T data) {
        return MyApiResponse.<T>builder()
                .success(true)
                .message("Success")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> MyApiResponse<T> success(String message, T data) {
        return MyApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> MyApiResponse<T> success(String message) {
        return MyApiResponse.<T>builder()
                .success(true)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Error response builders
    public static <T> MyApiResponse<T> error(String message) {
        return MyApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> MyApiResponse<T> error(String message, String code, String details) {
        return MyApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(ErrorDetails.builder()
                        .code(code)
                        .details(details)
                        .build())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> MyApiResponse<T> error(String message, String code, String details, Object validationErrors) {
        return MyApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(ErrorDetails.builder()
                        .code(code)
                        .details(details)
                        .validationErrors(validationErrors)
                        .build())
                .timestamp(LocalDateTime.now())
                .build();
    }
}