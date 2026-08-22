package com.app.server.mapper;


import com.app.shared.events.AuthorData;
import com.app.shared.events.CommentEventDto;
import com.app.shared.events.type.CommentActionType;
import com.app.server.dto.response.comment.CommentResponseDto;
import com.app.server.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CommentMapper {
    private final UserMapper userMapper;

    // Creation and editing now live on the Comment aggregate (Comment.writeOn / replyTo / edit).
    // What remains here is one-directional: domain object out to a DTO or a published event.

    public CommentEventDto toCommentEventDto(Comment comment, CommentActionType actionType) {
        AuthorData authorData = null;
        if (comment.getAuthor() != null) {
            authorData = AuthorData.builder()
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
