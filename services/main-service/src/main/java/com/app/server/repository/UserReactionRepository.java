package com.app.server.repository;

import com.app.server.model.UserReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReactionRepository extends JpaRepository<UserReaction,Long> {
    Long countByTargetId(Long postId);
}
