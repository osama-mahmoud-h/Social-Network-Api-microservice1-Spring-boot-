package com.app.server.service;


import com.app.server.dto.request.reaction.ReactToEntityRequestDto;
import com.app.server.model.UserProfile;

public interface ReactionService {
    Boolean reactToPost(Long currentUserId, ReactToEntityRequestDto reactToEntityRequestDto, Long postId);

    //reactToComment method is not implemented in the original code snippet
    Boolean reactToComment(Long currentUserId, ReactToEntityRequestDto reactToEntityRequestDto, Long commentId);
}
