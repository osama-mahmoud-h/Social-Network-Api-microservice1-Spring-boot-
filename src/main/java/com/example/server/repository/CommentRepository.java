package com.example.server.repository;

import com.example.server.model.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.postId=:postId")
    List<Comment> findCommentByPostId(@Param("postId")Long postId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.parentComment.commentId=:commentId")
    List<Comment> findRepliesOnComments(@Param("commentId")Long CommentId, Pageable pageable);
}
