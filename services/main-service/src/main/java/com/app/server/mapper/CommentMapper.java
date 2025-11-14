package com.app.server.mapper;


import com.app.server.dto.request.comment.AddNewCommentRequestDto;
import com.app.server.dto.request.comment.UpdateCommentRequestDto;
import com.app.server.dto.response.comment.CommentResponseDto;
import com.app.server.model.Post;
import com.app.server.model.UserProfile;
import com.app.server.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CommentMapper {
    private final UserMapper userMapper;

    public Comment mapAddNewCommentRequestDtoToComment(UserProfile author, Post post, AddNewCommentRequestDto addNewCommentRequestDto) {
        return Comment.builder()
                .post(post)
                .author(author)
                .parentComment(null)
                .createdAt(Instant.now())
                .updatedAt(null)
                .content(addNewCommentRequestDto.getContent())
                .build();
    }

    public Comment mapUpdateCommentRequestDtoToComment(Comment comment, UpdateCommentRequestDto updateCommentRequestDto) {
       comment.setUpdatedAt(Instant.now());
       if(updateCommentRequestDto.getContent() != null) {
           comment.setContent(updateCommentRequestDto.getContent());
       }
       return comment;
    }

    public CommentResponseDto mapCommentToCommentResponseDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .parentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null)
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .postId(comment.getPost() != null ? comment.getPost().getPostId() : null)
                .author(userMapper.mapToAuthorResponseDto(comment.getAuthor()))
                .replies(Collections.emptySet()) // Replies should be fetched separately via getCommentReplies endpoint
                .build();
    }
}
