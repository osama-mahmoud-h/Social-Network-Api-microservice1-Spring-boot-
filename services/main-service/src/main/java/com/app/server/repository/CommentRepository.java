package com.app.server.repository;

import com.app.server.model.Comment;
import org.apache.commons.lang3.function.Failable;
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

    @Query("""
            SELECT c FROM Comment c
            LEFT JOIN FETCH c.author
            LEFT JOIN FETCH c.post
            LEFT JOIN FETCH c.parentComment
            WHERE c.post.postId = :postId AND c.parentComment IS NULL
            """)
    List<Comment> findCommentByPostId(@Param("postId")Long postId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.parentComment.commentId=:commentId")
    List<Comment> findRepliesOnComments(@Param("commentId")Long CommentId, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.author.userId = :userId AND c.commentId = :commentId")
    int deleteByIdAndAuthorId(@Param("commentId") Long commentId, @Param("userId") Long userId);


    @Query("SELECT c FROM Comment c WHERE c.commentId=:commentId AND c.author.userId=:userId")
    Optional<Comment> findByIdAndAuthorId(@Param("userId") Long userId, @Param("commentId") Long commentId);

    @Query("""
            SELECT c FROM Comment c
            LEFT JOIN FETCH c.author
            LEFT JOIN FETCH c.post
            WHERE c.parentComment.commentId = :parentCommentId
            """)
    List<Comment> findCommentByParentCommentId(@Param("parentCommentId") Long parentCommentId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.postId = :postId")
    Long countByPostId(@Param("postId") Long postId);

    @Query("""
            SELECT c FROM Comment c
            JOIN FETCH c.author a
            JOIN FETCH c.post p
            LEFT JOIN FETCH c.parentComment pc
            WHERE c.commentId IN :commentIds
    """)
    List<Comment> getAllByIds(List<Long> commentIds, Pageable pageable);
}
