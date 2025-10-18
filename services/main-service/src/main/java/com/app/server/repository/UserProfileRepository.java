package com.app.server.repository;

import com.app.server.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByEmail(String email);

    Optional<UserProfile> findUserByUserId(Long userId);

    Optional<UserProfile> findUserByEmail(String email);

    Optional<UserProfile> getAppUsersByUserId(Long currentUser);
}