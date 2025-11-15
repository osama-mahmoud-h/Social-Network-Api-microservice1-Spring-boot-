package com.app.server.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simplified DTO for post information used in internal API calls
 * Contains only essential fields to minimize data transfer
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostInfoDto {
    private Long postId;
    private Long authorId;
    private String content;
}