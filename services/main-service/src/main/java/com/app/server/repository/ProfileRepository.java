package com.app.server.repository;

import com.app.server.model.Profile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile,Long> {

    @Query("SELECT pr FROM profiles pr WHERE pr.user.userId = :userId")
    Optional<Profile> findByUserId(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query("UPDATE profiles pr SET pr.bio = :bio WHERE pr.profileId = :profileId")
    void updateBio(@Param("profileId") Long profileId, @Param("bio") String bio);

    @Transactional
    @Modifying
    @Query("UPDATE profiles pr SET pr.imageUrl = :imageUrl WHERE pr.profileId = :profileId")
    void updateImage(@Param("profileId") Long profileId, @Param("imageUrl") String imageUrl);
}
