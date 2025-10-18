package com.app.server.service.impl;

import com.app.server.dto.request.reaction.ReactToEntityRequestDto;
import com.app.server.enums.ReactionTargetType;
import com.app.server.exception.CustomRuntimeException;
import com.app.server.mapper.UserReactionMapper;
import com.app.server.model.UserProfile;
import com.app.server.model.UserReaction;
import com.app.server.repository.CommentRepository;
import com.app.server.repository.PostRepository;
import com.app.server.repository.UserReactionsRepository;
import com.app.server.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

    private final UserReactionsRepository userReactionsRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserReactionMapper reactionMapper;

    @Override
    public Boolean reactToPost(UserProfile userProfile, ReactToEntityRequestDto reactToEntityRequestDto, Long postId) {
        postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
        Optional<UserReaction> oldPostReaction = userReactionsRepository.findByAuthorAndTargetIdAndReactionTargetType(
                userProfile.getUserId(),
                postId,
                ReactionTargetType.POST.toString()
        );

        if(isFirstReaction(oldPostReaction)) {
            UserReaction userReactedPost = reactionMapper.mapToUserPostReaction(reactToEntityRequestDto, userProfile, postId);
            userReactionsRepository.save(userReactedPost);
            return true;
        }else if(oldPostReaction.isPresent() && isTheSameReaction(oldPostReaction.get(), reactToEntityRequestDto)) {
            this.deleteOldReaction(oldPostReaction.get());
            return true;
        }
        this.updatePostReaction(oldPostReaction, reactToEntityRequestDto);
        return true;
    }

    //reactToComment method is not implemented in the original code snippet
    @Override
    public Boolean reactToComment(UserProfile userProfile, ReactToEntityRequestDto reactToEntityRequestDto, Long commentId) {
        commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        Optional<UserReaction> oldCommentReaction = userReactionsRepository.findByAuthorAndTargetIdAndReactionTargetType(
                userProfile.getUserId(),
                commentId,
                ReactionTargetType.COMMENT.toString()
        );

        if (isFirstReaction(oldCommentReaction)) {
            UserReaction userReactedComment = reactionMapper.mapToUserCommentReaction(reactToEntityRequestDto, userProfile, commentId);
            userReactionsRepository.save(userReactedComment);
            return true;
        } else if (oldCommentReaction.isPresent() && isTheSameReaction(oldCommentReaction.get(), reactToEntityRequestDto)) {
            this.deleteOldReaction(oldCommentReaction.get());
            return true;
        }
        this.updateCommentReaction(oldCommentReaction, reactToEntityRequestDto);
        return true;
    }

    private boolean isFirstReaction(Optional<UserReaction> reaction) {
        return reaction.isEmpty();
    }
    private void updatePostReaction(Optional<UserReaction> oldPostReaction, ReactToEntityRequestDto reactToEntityRequestDto) {
        oldPostReaction.orElseThrow(()->new CustomRuntimeException("Internal Server Error"))
                .setReactionType(reactToEntityRequestDto.getReactionType());
        this.userReactionsRepository.updateReaction(oldPostReaction.get().getReactionId(), reactToEntityRequestDto.getReactionType().toString());
    }
    private boolean isTheSameReaction(UserReaction oldPostReaction, ReactToEntityRequestDto reactToEntityRequestDto) {
        return oldPostReaction.getReactionType().equals(reactToEntityRequestDto.getReactionType());
    }
    private void deleteOldReaction(UserReaction oldPostReaction) {
        userReactionsRepository.delete(oldPostReaction);
    }
    private void updateCommentReaction(Optional<UserReaction> oldCommentReaction, ReactToEntityRequestDto reactToEntityRequestDto) {
        oldCommentReaction.orElseThrow(() -> new CustomRuntimeException("Internal Server Error"))
                .setReactionType(reactToEntityRequestDto.getReactionType());
        this.userReactionsRepository.updateReaction(oldCommentReaction.get().getReactionId(), reactToEntityRequestDto.getReactionType().toString());
    }


}
