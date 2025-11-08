package com.app.auth.service.impl;

import com.app.auth.dto.request.SendOtpRequest;
import com.app.auth.dto.request.VerifyOtpRequest;
import com.app.auth.dto.response.OtpResponse;
import com.app.auth.enums.OtpStatus;
import com.app.auth.mapper.OtpMapper;
import com.app.auth.model.Otp;
import com.app.auth.repository.OtpRepository;
import com.app.auth.service.EmailService;
import com.app.auth.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final OtpMapper otpMapper;
    private final EmailService emailService;

    @Override
    public OtpResponse sendOtp(SendOtpRequest request) {
        // Delete any existing OTP for this email and type
        otpRepository.deleteByEmailAndType(request.getEmail(), request.getType());

        // Generate new OTP
        Otp otp = otpMapper.buildOtp(request);
        otpRepository.save(otp);

        // Send OTP via email using strategy pattern
        String purpose = getPurposeText(request.getType());
        emailService.sendOtpEmail(request.getEmail(), otp.getCode(), purpose);

        log.info("OTP sent to email: {} for type: {}", request.getEmail(), request.getType());

        return OtpResponse.success(
                "OTP sent successfully to " + request.getEmail(),
                otp.getExpiresAt()
        );
    }

    @Override
    public OtpResponse verifyOtp(VerifyOtpRequest request) {
        Optional<Otp> otpOptional = otpRepository.findByEmailAndTypeAndStatus(
                request.getEmail(),
                request.getType(),
                OtpStatus.PENDING
        );

        if (otpOptional.isEmpty()) {
            log.warn("OTP not found or already used for email: {}", request.getEmail());
            return OtpResponse.invalid("OTP not found or already used");
        }

        Otp otp = otpOptional.get();

        // Check if OTP is expired
        if (otp.getExpiresAt().isBefore(Instant.now())) {
            otp.setStatus(OtpStatus.EXPIRED);
            otpRepository.save(otp);
            log.warn("OTP expired for email: {}", request.getEmail());
            return OtpResponse.expired("OTP has expired. Please request a new one");
        }

        // Verify OTP code
        if (!otp.getCode().equals(request.getCode())) {
            log.warn("Invalid OTP code provided for email: {}", request.getEmail());
            return OtpResponse.invalid("Invalid OTP code");
        }

        // Mark OTP as verified
        otp.setStatus(OtpStatus.VERIFIED);
        otpRepository.save(otp);

        log.info("OTP verified successfully for email: {}", request.getEmail());
        return OtpResponse.verified("OTP verified successfully");
    }

    private String getPurposeText(com.app.auth.enums.OtpType type) {
        return switch (type) {
            case REGISTRATION -> "Registration";
            case PASSWORD_RESET -> "Password Reset";
            case EMAIL_VERIFICATION -> "Email Verification";
        };
    }
}
