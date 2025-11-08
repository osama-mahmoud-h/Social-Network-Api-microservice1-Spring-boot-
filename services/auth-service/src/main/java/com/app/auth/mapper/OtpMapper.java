package com.app.auth.mapper;

import com.app.auth.dto.request.SendOtpRequest;
import com.app.auth.enums.OtpStatus;
import com.app.auth.model.Otp;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;

@Component
public class OtpMapper {

    private static final int OTP_LENGTH = 6;
    private static final long OTP_EXPIRY_SECONDS = 600; // 10 minutes
    private final SecureRandom secureRandom = new SecureRandom();

    public Otp buildOtp(SendOtpRequest request) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(OTP_EXPIRY_SECONDS);

        return Otp.builder()
                .email(request.getEmail())
                .code(generateOtpCode())
                .type(request.getType())
                .status(OtpStatus.PENDING)
                .createdAt(now)
                .expiresAt(expiresAt)
                .build();
    }

    private String generateOtpCode() {
        int otp = secureRandom.nextInt(900000) + 100000; // Generates 6-digit number
        return String.valueOf(otp);
    }
}
