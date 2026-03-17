package com.app.auth.scheduler;

import com.app.auth.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventCleanupScheduler {

    private final OutboxEventRepository outboxEventRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupOldEvents() {
        try {
            Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
            int deletedCount = outboxEventRepository.deleteByCreatedAtBefore(sevenDaysAgo);
            log.info("Cleaned up {} outbox events older than 7 days", deletedCount);
        } catch (Exception e) {
            log.error("Error during outbox event cleanup: {}", e.getMessage(), e);
        }
    }
}