package com.app.server.projection;

import java.time.Instant;

/**
 * Projection for post details query
 * Type-safe alternative to Object[]
 */
public interface PostDetailProjection {
    Long getPostId();
    String getContent();
    Long getCommentsCount();
    Long getReactionsCount();
    Instant getCreatedAt();
    Instant getUpdatedAt();

    // JSON strings (parsed later)
    String getAuthor();
    String getMyReactionType();
    String getFiles();  // JSON array of files
}