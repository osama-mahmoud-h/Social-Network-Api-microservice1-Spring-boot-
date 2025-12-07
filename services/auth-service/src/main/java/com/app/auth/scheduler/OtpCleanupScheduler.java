package com.app.auth.scheduler;

import com.app.auth.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class OtpCleanupScheduler {

    private final OtpRepository otpRepository;

    /**
     * Cleanup expired OTPs every hour
     * Cron: At minute 0 past every day
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupExpiredOtps() {
        try {
            Instant now = Instant.now();
            otpRepository.deleteExpiredOtps(now);
            log.info("Expired OTPs cleanup completed successfully");
        } catch (Exception e) {
            log.error("Error during OTP cleanup: {}", e.getMessage(), e);
        }
    }
}
