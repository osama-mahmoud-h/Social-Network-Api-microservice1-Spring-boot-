package com.app.auth.repository;

import com.app.auth.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND (t.expired = false OR t.revoked = false)")
    List<Token> findAllValidTokenByUser(Long userId);

    Optional<Token> findByToken(String token);

    @Modifying
    @Query("DELETE FROM Token t WHERE t.expiresAt < CURRENT_TIMESTAMP")
    void deleteExpiredTokens();
}
