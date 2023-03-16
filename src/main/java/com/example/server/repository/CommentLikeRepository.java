package com.example.server.repository;

import com.example.server.models.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {
    @Modifying
    @Query(value = "DELETE FROM users_like_comments where liker_id=?1 AND comment_id=?2",nativeQuery = true)
    void deleteLikeOnComment(Long liker_id,Long post_id);

    @Modifying
    @Query(value = "delete from users_like_comments where id =?1",nativeQuery = true)
    void deleteById(Long id);
}
