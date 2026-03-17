package com.app.auth.repository;

import com.app.auth.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    @Modifying
    @Query("DELETE FROM OutboxEvent e WHERE e.createdAt < :before")
    int deleteByCreatedAtBefore(Instant before);
}