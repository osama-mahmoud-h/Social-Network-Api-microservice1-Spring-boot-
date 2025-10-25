package com.app.server.event.domain;

import com.app.server.enums.PostActionType;
import com.app.server.model.Post;
import lombok.Getter;

/**
 * Domain event for post-related actions (CREATE, UPDATE, DELETE)
 */
@Getter
public class PostDomainEvent extends DomainEvent {
    private final PostActionType actionType;
    private final Post post;
    private final Long postId;

    public PostDomainEvent(Long userId, PostActionType actionType, Post post) {
        super(userId);
        this.actionType = actionType;
        this.post = post;
        this.postId = post.getPostId();
    }

    @Override
    public String getEventType() {
        return "POST_" + actionType.name();
    }
}
