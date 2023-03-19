package com.example.server.repository;

import com.example.server.models.CommentReplay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentsReplayRepository extends JpaRepository<CommentReplay,Long> {
    @Modifying
    @Query(value = "DELETE FROM comments_replies where author_id=?1 AND comment_id=?2",nativeQuery = true)
    void deleteReplayOnComment(Long author_id,Long comment_id);

    @Modifying
    @Query(value = "delete from comments_replies where id =?1",nativeQuery = true)
    void deleteById(Long replay_id);

    boolean existsById(Long id);
}
