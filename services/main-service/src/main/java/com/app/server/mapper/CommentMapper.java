package com.app.server.mapper;


import com.app.server.dto.notification.comment.CommentEventDto;
import com.app.server.enums.CommentActionType;
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

    public CommentEventDto toCommentEventDto(Comment comment, CommentActionType actionType) {
        CommentEventDto.AuthorData authorData = null;
        if (comment.getAuthor() != null) {
            authorData = CommentEventDto.AuthorData.builder()
                    .userId(comment.getAuthor().getUserId())
                    .firstName(comment.getAuthor().getFirstName())
                    .lastName(comment.getAuthor().getLastName())
                    .build();
        }

        Long postAuthorId = null;
        if (comment.getPost() != null && comment.getPost().getAuthor() != null) {
            postAuthorId = comment.getPost().getAuthor().getUserId();
        }

        CommentEventDto.CommentData commentData = CommentEventDto.CommentData.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt() != null ? comment.getCreatedAt().getEpochSecond() : null)
                .updatedAt(comment.getUpdatedAt() != null ? comment.getUpdatedAt().getEpochSecond() : null)
                .postId(comment.getPost() != null ? comment.getPost().getPostId() : null)
                .postAuthorId(postAuthorId)
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null)
                .author(authorData)
                .build();

        return CommentEventDto.builder()
                .actionType(actionType)
                .commentId(comment.getCommentId())
                .comment(commentData)
                .build();
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
