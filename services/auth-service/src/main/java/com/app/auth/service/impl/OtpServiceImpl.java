package com.app.auth.service.impl;

import com.app.auth.model.dto.request.SendOtpRequest;
import com.app.auth.model.dto.request.VerifyOtpRequest;
import com.app.auth.model.dto.response.OtpResponse;
import com.app.auth.model.enums.NotificationChannel;
import com.app.auth.model.enums.OtpStatus;
import com.app.auth.mapper.OtpMapper;
import com.app.auth.model.entity.Otp;
import com.app.auth.model.enums.OtpType;
import com.app.auth.repository.OtpRepository;
import com.app.auth.service.notification.NotificationStrategyFactory;
import com.app.auth.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final OtpMapper otpMapper;
    private final NotificationStrategyFactory notificationStrategyFactory;

    @Override
    public Otp generateOtp(SendOtpRequest request) {
        otpRepository.deleteByEmailAndType(request.getEmail(), request.getType());
        Otp otp = otpMapper.buildOtp(request);
        return otpRepository.save(otp);
    }

    @Override
    public OtpResponse sendOtp(SendOtpRequest request) {
        Otp otp = generateOtp(request);
        String purpose = getPurposeText(request.getType());
        
        // request.getSelectedChannel() will ALWAYS have a value because of @Builder.Default!
        // Default is EMAIL globally for all events, but easily overridden from frontend JSON.
        NotificationChannel channel = request.getSelectedChannel();
        
        Map<String, Object> variables = Map.of(
            "purpose", purpose,
            "otpCode", otp.getCode()
        );

        notificationStrategyFactory
            .getSender(channel)
            .sendTemplateNotification(
                request.getEmail(), 
                "Your OTP Code - " + purpose, 
                "otp-email", 
                variables
            );

        log.info("OTP sent via {} to destination: {} for purpose: {}", channel, request.getEmail(), purpose);
        return OtpResponse.success("OTP sent successfully to " + request.getEmail(), otp.getExpiresAt());
    }

    @Override
    public OtpResponse verifyOtp(VerifyOtpRequest request) {
        Optional<Otp> otpOptional = otpRepository.findByEmailAndTypeAndStatus(
                request.getEmail(), request.getType(), OtpStatus.PENDING);

        if (otpOptional.isEmpty()) {
            log.warn("OTP not found or already used for email: {}", request.getEmail());
            return OtpResponse.invalid("OTP not found or already used");
        }

        Otp otp = otpOptional.get();

        if (otp.getExpiresAt().isBefore(Instant.now())) {
            otp.setStatus(OtpStatus.EXPIRED);
            otpRepository.save(otp);
            log.warn("OTP expired for email: {}", request.getEmail());
            return OtpResponse.expired("OTP has expired. Please request a new one");
        }

        if (!otp.getCode().equals(request.getCode())) {
            log.warn("Invalid OTP code provided for email: {}", request.getEmail());
            return OtpResponse.invalid("Invalid OTP code");
        }

        otp.setStatus(OtpStatus.VERIFIED);
        otpRepository.save(otp);

        log.info("OTP verified successfully for email: {}", request.getEmail());
        return OtpResponse.verified("OTP verified successfully");
    }

    private String getPurposeText(OtpType type) {
        return switch (type) {
            case REGISTRATION -> "Registration";
            case PASSWORD_RESET -> "Password Reset";
            case EMAIL_VERIFICATION -> "Email Verification";
        };
    }
}
