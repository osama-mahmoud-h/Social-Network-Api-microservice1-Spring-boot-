package com.app.server.mapper;


import com.app.server.dto.request.comment.AddNewCommentRequestDto;
import com.app.server.dto.request.comment.UpdateCommentRequestDto;
import com.app.server.dto.response.comment.CommentResponseDto;
import com.app.server.model.UserProfile;
import com.app.server.model.Comment;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CommentMapper {
    public Comment mapAddNewCommentRequestDtoToComment(UserProfile author, AddNewCommentRequestDto addNewCommentRequestDto) {
        return Comment.builder()
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
    public CommentResponseDto mapCommentToCommentResponseDto(Object comment) {
        return null;
    }
}
