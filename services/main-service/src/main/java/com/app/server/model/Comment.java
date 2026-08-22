package com.app.server.model;

import com.app.server.exception.IllegalCommentOperation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * A comment on a post, or a reply to such a comment.
 *
 * <p>The thread is deliberately two levels deep and no more: {@link #replyTo} refuses a parent that
 * is itself a reply. That rule used to live in the service, where nothing stopped a second caller
 * from saving a third-level reply directly.
 *
 * <p>Only the author may edit or delete. That check is now part of the transition rather than
 * something a query is expected to enforce — see the note on {@link #deleteBy}.
 */
@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Comment {

    private static final int MAX_CONTENT_LENGTH = 255;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_sequence")
    @SequenceGenerator(name = "comment_sequence", sequenceName = "comment_sequence", allocationSize = 50)
    @Column(name = "comment_id")
    private Long commentId;

    @Column(length = MAX_CONTENT_LENGTH, nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", referencedColumnName = "user_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_comments_author_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserProfile author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", referencedColumnName = "post_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_comments_post_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id", referencedColumnName = "comment_id", nullable = true, updatable = false, foreignKey = @ForeignKey(name = "FK_comments_parent_comment_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Comment> replies;

    /** A top-level comment on a post. */
    public static Comment writeOn(Post post, UserProfile author, String content) {
        Comment comment = new Comment();
        comment.post = Objects.requireNonNull(post, "post");
        comment.author = Objects.requireNonNull(author, "author");
        comment.content = requireValidContent(content);
        comment.createdAt = Instant.now();
        return comment;
    }

    /**
     * A reply to an existing top-level comment. The reply belongs to the same post as its parent —
     * the caller does not get to pick, which is what keeps a reply from landing under a foreign post.
     */
    public static Comment replyTo(Comment parentComment, UserProfile author, String content) {
        Objects.requireNonNull(parentComment, "parentComment");
        if (parentComment.isReply()) {
            throw IllegalCommentOperation.rejected("You cannot reply to a reply");
        }

        Comment reply = writeOn(parentComment.getPost(), author, content);
        reply.parentComment = parentComment;
        return reply;
    }

    /** Change the text. Only the author may do so. */
    public void edit(Long actorId, String newContent) {
        requireAuthor(actorId, "edit");
        this.content = requireValidContent(newContent);
        this.updatedAt = Instant.now();
    }

    /**
     * Assert that this actor is allowed to delete the comment. The aggregate cannot remove itself
     * from the repository, so the caller deletes — but only after this has passed. Previously the
     * authorisation rode along in a bulk {@code DELETE ... WHERE author_id = ?} whose arguments were
     * being passed in the wrong order, so the delete silently matched nothing.
     */
    public void deleteBy(Long actorId) {
        requireAuthor(actorId, "delete");
    }

    public boolean isReply() {
        return parentComment != null;
    }

    public boolean isAuthoredBy(Long userId) {
        return author != null && author.getUserId().equals(userId);
    }

    private void requireAuthor(Long actorId, String action) {
        if (!isAuthoredBy(actorId)) {
            throw IllegalCommentOperation.notTheAuthor(action);
        }
    }

    private static String requireValidContent(String content) {
        if (content == null || content.isBlank()) {
            throw IllegalCommentOperation.rejected("Comment content cannot be empty");
        }
        String trimmed = content.trim();
        if (trimmed.length() > MAX_CONTENT_LENGTH) {
            throw IllegalCommentOperation.rejected(
                    "Comment content cannot exceed " + MAX_CONTENT_LENGTH + " characters");
        }
        return trimmed;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Comment that)) return false;
        return commentId != null && commentId.equals(that.commentId);
    }

    @Override
    public int hashCode() {
        // Constant so a comment keeps the same bucket before and after it is assigned an id.
        return Comment.class.hashCode();
    }
}