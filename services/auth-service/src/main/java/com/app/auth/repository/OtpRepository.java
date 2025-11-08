package com.app.auth.repository;

import com.app.auth.enums.OtpStatus;
import com.app.auth.enums.OtpType;
import com.app.auth.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findByEmailAndTypeAndStatus(String email, OtpType type, OtpStatus status);

    void deleteByEmail(String email);

    @Modifying
    @Query("DELETE FROM Otp o WHERE o.expiresAt < :now")
    void deleteExpiredOtps(Instant now);

    @Modifying
    @Query("DELETE FROM Otp o WHERE o.email = :email AND o.type = :type")
    void deleteByEmailAndType(String email, OtpType type);
}
