package com.app.server.service;


import com.app.server.dto.request.reaction.ReactToEntityRequestDto;
import com.app.server.model.AppUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

public interface ReactionService {
    Object reactToPost(AppUser appUser, ReactToEntityRequestDto reactToEntityRequestDto, Long postId);

    //reactToComment method is not implemented in the original code snippet
    Boolean reactToComment(AppUser appUser, ReactToEntityRequestDto reactToEntityRequestDto, Long commentId);
}
