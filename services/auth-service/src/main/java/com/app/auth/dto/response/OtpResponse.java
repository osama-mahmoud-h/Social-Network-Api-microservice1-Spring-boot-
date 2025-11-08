package com.app.auth.dto.response;

import com.app.auth.enums.OtpStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpResponse {
    private OtpStatus status;
    private String message;
    private Instant expiresAt;

    public static OtpResponse success(String message, Instant expiresAt) {
        return OtpResponse.builder()
                .status(OtpStatus.PENDING)
                .message(message)
                .expiresAt(expiresAt)
                .build();
    }

    public static OtpResponse verified(String message) {
        return OtpResponse.builder()
                .status(OtpStatus.VERIFIED)
                .message(message)
                .build();
    }

    public static OtpResponse invalid(String message) {
        return OtpResponse.builder()
                .status(OtpStatus.INVALID)
                .message(message)
                .build();
    }

    public static OtpResponse expired(String message) {
        return OtpResponse.builder()
                .status(OtpStatus.EXPIRED)
                .message(message)
                .build();
    }
}
