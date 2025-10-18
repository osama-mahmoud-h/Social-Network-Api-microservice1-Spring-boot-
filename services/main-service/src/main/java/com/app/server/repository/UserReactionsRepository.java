package com.app.server.repository;


import com.app.server.model.UserReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserReactionsRepository  extends JpaRepository<UserReaction, Long> {

    @Query("SELECT ur FROM UserReaction ur WHERE ur.author.userId = :authorId AND ur.targetId = :targetId AND ur.reactionTargetType = :reactionTargetType")
    Optional<UserReaction> findByAuthorAndTargetIdAndReactionTargetType(@Param("authorId") Long authorId,
                                                                        @Param("targetId") Long targetId,
                                                                        @Param("reactionTargetType") String reactionTargetType);

    @Modifying
    @Transactional
    @Query("UPDATE UserReaction ur SET ur.reactionType = :reactionType WHERE ur.reactionId = :reactionId")
    int updateReaction(@Param("reactionId") Long reactionId, @Param("reactionType")String reactionType);
}
