package com.app.server.service.impl;

import com.app.server.dto.request.reaction.ReactToEntityRequestDto;
import com.app.server.enums.ReactionActionType;
import com.app.server.enums.ReactionTargetType;
import com.app.server.event.app.domain.ReactionDomainEvent;
import com.app.server.exception.CustomRuntimeException;
import com.app.server.mapper.UserReactionMapper;
import com.app.server.model.Comment;
import com.app.server.model.UserProfile;
import com.app.server.model.UserReaction;
import com.app.server.repository.CommentRepository;
import com.app.server.repository.PostRepository;
import com.app.server.repository.UserProfileRepository;
import com.app.server.repository.UserReactionsRepository;
import com.app.server.service.ReactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

    private final UserReactionsRepository userReactionsRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserProfileRepository userProfileRepository;
    private final UserReactionMapper reactionMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Boolean reactToPost(Long currentUserId, ReactToEntityRequestDto request, Long postId) {
        log.info("User [{}] reacting to post [{}] with reaction [{}]", currentUserId, postId, request.getReactionType());

        UserProfile userProfile = getUserProfile(currentUserId);

        postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.warn("Post not found with id [{}]", postId);
                    return new CustomRuntimeException("Post not found", HttpStatus.NOT_FOUND);
                });

        Optional<UserReaction> existingReaction = findExistingReaction(currentUserId, postId, ReactionTargetType.POST);
        log.debug("Existing reaction for user [{}] on post [{}]: {}", currentUserId, postId,
                existingReaction.map(r -> r.getReactionType().toString()).orElse("none"));

        try {
            ReactionActionType actionTaken = applyReactionChange(existingReaction, request,
                    () -> userReactionsRepository.save(reactionMapper.mapToUserPostReaction(request, userProfile, postId)));

            log.info("Reaction action [{}] applied for user [{}] on post [{}]", actionTaken, currentUserId, postId);

            publishReactionEvent(currentUserId, actionTaken, request.getReactionType(),
                    ReactionTargetType.POST, postId, postId);
        } catch (DataAccessException ex) {
            log.error("Database error while processing reaction for user [{}] on post [{}]: {}", currentUserId, postId, ex.getMessage(), ex);
            throw new CustomRuntimeException("Failed to process reaction on post", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return true;
    }

    @Override
    @Transactional
    public Boolean reactToComment(Long currentUserId, ReactToEntityRequestDto request, Long commentId) {
        log.info("User [{}] reacting to comment [{}] with reaction [{}]", currentUserId, commentId, request.getReactionType());

        UserProfile userProfile = getUserProfile(currentUserId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> {
                    log.warn("Comment not found with id [{}]", commentId);
                    return new CustomRuntimeException("Comment not found", HttpStatus.NOT_FOUND);
                });

        Optional<UserReaction> existingReaction = findExistingReaction(currentUserId, commentId, ReactionTargetType.COMMENT);
        log.debug("Existing reaction for user [{}] on comment [{}]: {}", currentUserId, commentId,
                existingReaction.map(r -> r.getReactionType().toString()).orElse("none"));

        try {
            ReactionActionType actionTaken = applyReactionChange(existingReaction, request,
                    () -> userReactionsRepository.save(reactionMapper.mapToUserCommentReaction(request, userProfile, commentId)));

            log.info("Reaction action [{}] applied for user [{}] on comment [{}]", actionTaken, currentUserId, commentId);

            publishReactionEvent(currentUserId, actionTaken, request.getReactionType(),
                    ReactionTargetType.COMMENT, commentId, comment.getPost().getPostId());
        } catch (DataAccessException ex) {
            log.error("Database error while processing reaction for user [{}] on comment [{}]: {}", currentUserId, commentId, ex.getMessage(), ex);
            throw new CustomRuntimeException("Failed to process reaction on comment", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return true;
    }

    private @NonNull UserProfile getUserProfile(Long currentUserId) {
        log.debug("Fetching user profile for userId [{}]", currentUserId);
        return userProfileRepository.findById(currentUserId)
                .orElseThrow(() -> {
                    log.warn("User profile not found for userId [{}]", currentUserId);
                    return new CustomRuntimeException("User Not Found", HttpStatus.NOT_FOUND);
                });
    }

    private Optional<UserReaction> findExistingReaction(Long userId, Long targetId, ReactionTargetType targetType) {
        log.debug("Looking up existing reaction for user [{}] on {} [{}]", userId, targetType, targetId);
        return userReactionsRepository.findByAuthorAndTargetIdAndReactionTargetType(userId, targetId, targetType);
    }

    private ReactionActionType applyReactionChange(Optional<UserReaction> existingReaction,
                                                    ReactToEntityRequestDto request,
                                                    Runnable saveNewReaction) {
        if (existingReaction.isEmpty()) {
            log.debug("No existing reaction found — saving new reaction [{}]", request.getReactionType());
            saveNewReaction.run();
            return ReactionActionType.ADDED;
        }

        UserReaction existing = existingReaction.get();
        if (existing.getReactionType().equals(request.getReactionType())) {
            log.debug("Same reaction [{}] — removing it (toggle off)", existing.getReactionType());
            userReactionsRepository.delete(existing);
            return ReactionActionType.REMOVED;
        }

        log.debug("Different reaction — updating from [{}] to [{}]", existing.getReactionType(), request.getReactionType());
        userReactionsRepository.updateReaction(existing.getReactionId(), request.getReactionType());
        return ReactionActionType.ADDED;
    }

    private void publishReactionEvent(Long reactorUserId, ReactionActionType actionType,
                                       com.app.server.enums.ReactionType reactionType,
                                       ReactionTargetType targetType, Long targetId, Long postId) {
        log.debug("Publishing reaction event: user [{}], action [{}], target {} [{}]", reactorUserId, actionType, targetType, targetId);
        eventPublisher.publishEvent(new ReactionDomainEvent(
                reactorUserId, actionType,
                actionType == ReactionActionType.REMOVED ? null : reactionType,
                targetType, targetId, postId));
    }
}
