package com.app.server.event.domain;

import com.app.server.enums.CommentActionType;
import com.app.server.model.Comment;
import lombok.Getter;

/**
 * Domain event for comment-related actions (CREATE, UPDATE, DELETE, REPLY)
 */
@Getter
public class CommentDomainEvent extends DomainEvent {
    private final CommentActionType actionType;
    private final Comment comment;
    private final Long commentId;
    private final Long postId;

    public CommentDomainEvent(Long userId, CommentActionType actionType, Comment comment) {
        super(userId);
        this.actionType = actionType;
        this.comment = comment;
        this.commentId = comment.getCommentId();
        this.postId = comment.getPost().getPostId();
    }

    @Override
    public String getEventType() {
        return "COMMENT_" + actionType.name();
    }
}
