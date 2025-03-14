package com.app.server.mapper;


import com.app.server.dto.request.reaction.ReactToEntityRequestDto;
import com.app.server.enums.ReactionTargetType;
import com.app.server.model.AppUser;
import com.app.server.model.Post;
import com.app.server.model.UserReaction;
import org.springframework.stereotype.Service;

@Service
public class UserReactionMapper {

    public UserReaction mapToUserPostReaction(ReactToEntityRequestDto reactToEntityRequestDto,
                                              AppUser author,
                                              Long postId) {
        return UserReaction.builder()
                .reactionType(reactToEntityRequestDto.getReactionType())
                .reactionTargetType(ReactionTargetType.POST)
                .author(author)
                .targetId(postId)
                .build();

    }

    public UserReaction mapToUserCommentReaction(ReactToEntityRequestDto reactToEntityRequestDto,
                                                 AppUser author,
                                                 Long commentId) {
        return UserReaction.builder()
                .reactionType(reactToEntityRequestDto.getReactionType())
                .reactionTargetType(ReactionTargetType.COMMENT)
                .author(author)
                .targetId(commentId)
                .build();
    }
}
