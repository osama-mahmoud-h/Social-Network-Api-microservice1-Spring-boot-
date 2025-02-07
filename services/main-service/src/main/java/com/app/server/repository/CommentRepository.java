package com.app.server.repository;

import com.app.server.model.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.postId=:postId")
    List<Comment> findCommentByPostId(@Param("postId")Long postId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.parentComment.commentId=:commentId")
    List<Comment> findRepliesOnComments(@Param("commentId")Long CommentId, Pageable pageable);

    @Transactional
    @Modifying
    @Query("SELECT p FROM Post p WHERE p.author.userId = :userId AND p.postId = :postId")
    int deleteByIdAndAuthorId(@Param("postId") Long postId, @Param("userId") Long userId);


    @Query("SELECT c FROM Comment c WHERE c.commentId=:commentId AND c.author.userId=:userId")
    Optional<Comment> findByIdAndAuthorId(@Param("userId") Long userId, @Param("commentId") Long commentId);
}
