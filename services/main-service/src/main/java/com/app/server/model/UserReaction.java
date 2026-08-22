package com.app.server.model;


import com.app.server.enums.ReactionTargetType;
import com.app.server.enums.ReactionType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

/**
 * One user's reaction to one target — a post or a comment.
 *
 * <p>The target is referenced by ({@code reactionTargetType}, {@code targetId}) rather than by an
 * object association, so a reaction never drags a Post or Comment into its transaction. The pair is
 * only ever set together, by the factory that names the target type, which is why there is no way
 * to build a POST reaction that carries a comment id.
 *
 * <p>The only state that changes after creation is the reaction type: a user swaps LOVE for LIKE.
 * Everything else — who reacted, and what they reacted to — is fixed for the row's lifetime.
 */
@Entity
@Table(name = "reactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserReaction {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "reaction_sequence", sequenceName = "reaction_sequence", allocationSize = 50)  // Adjust allocationSize as needed
    private Long reactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reactionType;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.MERGE, optional = false)
    @JoinColumn(name = "author_id",
            referencedColumnName = "user_id",
            nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "FK_reactions_author_id" ))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserProfile author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionTargetType reactionTargetType;  // POST or COMMENT

    @Column(nullable = false)
    private Long targetId;  // postId or commentId

    /** A user reacts to a post. */
    public static UserReaction onPost(UserProfile author, Long postId, ReactionType reactionType) {
        return on(author, ReactionTargetType.POST, postId, reactionType);
    }

    /** A user reacts to a comment. */
    public static UserReaction onComment(UserProfile author, Long commentId, ReactionType reactionType) {
        return on(author, ReactionTargetType.COMMENT, commentId, reactionType);
    }

    private static UserReaction on(UserProfile author, ReactionTargetType targetType,
                                   Long targetId, ReactionType reactionType) {
        UserReaction reaction = new UserReaction();
        reaction.author = Objects.requireNonNull(author, "author");
        reaction.reactionTargetType = Objects.requireNonNull(targetType, "reactionTargetType");
        reaction.targetId = Objects.requireNonNull(targetId, "targetId");
        reaction.reactionType = Objects.requireNonNull(reactionType, "reactionType");
        return reaction;
    }

    /**
     * Swap this reaction for a different one. Re-applying the reaction the user already has is not a
     * change but a toggle-off, which is a delete — so the caller must decide with {@link #is} first
     * rather than calling this with the current value.
     */
    public void changeTo(ReactionType newReactionType) {
        Objects.requireNonNull(newReactionType, "newReactionType");
        if (is(newReactionType)) {
            throw new IllegalStateException("Reaction is already " + newReactionType);
        }
        this.reactionType = newReactionType;
    }

    /** Whether this is already the given reaction — the toggle-off test. */
    public boolean is(ReactionType candidate) {
        return this.reactionType == candidate;
    }

    public boolean isBy(Long userId) {
        return author != null && author.getUserId().equals(userId);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof UserReaction that)) return false;
        return reactionId != null && reactionId.equals(that.reactionId);
    }

    @Override
    public int hashCode() {
        // Constant so a reaction keeps the same bucket before and after it is assigned an id.
        return UserReaction.class.hashCode();
    }
}