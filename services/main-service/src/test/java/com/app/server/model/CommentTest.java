package com.app.server.model;

import com.app.server.exception.IllegalCommentOperation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The comment aggregate: who may write, edit and delete, and how deep a thread goes.
 * Pure unit test — no Spring, no database.
 */
class CommentTest {

    private static final Long AUTHOR_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;
    private static final Long POST_ID = 10L;

    private static UserProfile user(Long id) {
        UserProfile user = new UserProfile();
        user.setUserId(id);
        return user;
    }

    private static Post post() {
        return Post.builder().postId(POST_ID).author(user(OTHER_USER_ID)).build();
    }

    private static Comment comment() {
        return Comment.writeOn(post(), user(AUTHOR_ID), "the original text");
    }

    @Nested
    @DisplayName("writeOn")
    class WriteOn {

        @Test
        void opens_a_top_level_comment_on_the_post() {
            Comment comment = comment();

            assertThat(comment.getContent()).isEqualTo("the original text");
            assertThat(comment.getPost().getPostId()).isEqualTo(POST_ID);
            assertThat(comment.isAuthoredBy(AUTHOR_ID)).isTrue();
            assertThat(comment.isReply()).isFalse();
            assertThat(comment.getCreatedAt()).isNotNull();
            assertThat(comment.getUpdatedAt()).isNull();
        }

        @Test
        void trims_surrounding_whitespace() {
            assertThat(Comment.writeOn(post(), user(AUTHOR_ID), "  padded  ").getContent())
                    .isEqualTo("padded");
        }

        @Test
        void refuses_empty_content() {
            assertThatThrownBy(() -> Comment.writeOn(post(), user(AUTHOR_ID), "   "))
                    .isInstanceOf(IllegalCommentOperation.class)
                    .hasMessageContaining("cannot be empty");
        }

        @Test
        @DisplayName("refuses content longer than the column, instead of failing at flush time")
        void refuses_over_long_content() {
            String tooLong = "x".repeat(256);

            assertThatThrownBy(() -> Comment.writeOn(post(), user(AUTHOR_ID), tooLong))
                    .isInstanceOf(IllegalCommentOperation.class)
                    .hasMessageContaining("255");
        }
    }

    @Nested
    @DisplayName("replyTo")
    class ReplyTo {

        @Test
        void a_reply_inherits_the_parent_post() {
            Comment parent = comment();

            Comment reply = Comment.replyTo(parent, user(OTHER_USER_ID), "a reply");

            assertThat(reply.isReply()).isTrue();
            assertThat(reply.getParentComment()).isSameAs(parent);
            assertThat(reply.getPost().getPostId()).isEqualTo(POST_ID);
            assertThat(reply.isAuthoredBy(OTHER_USER_ID)).isTrue();
        }

        @Test
        @DisplayName("threads stop at two levels")
        void a_reply_cannot_be_replied_to() {
            Comment reply = Comment.replyTo(comment(), user(OTHER_USER_ID), "a reply");

            assertThatThrownBy(() -> Comment.replyTo(reply, user(AUTHOR_ID), "a nested reply"))
                    .isInstanceOf(IllegalCommentOperation.class)
                    .hasMessageContaining("reply to a reply");
        }
    }

    @Nested
    @DisplayName("edit")
    class Edit {

        @Test
        void the_author_can_change_the_text() {
            Comment comment = comment();

            comment.edit(AUTHOR_ID, "the new text");

            assertThat(comment.getContent()).isEqualTo("the new text");
            assertThat(comment.getUpdatedAt()).isNotNull();
        }

        @Test
        void nobody_else_can() {
            Comment comment = comment();

            assertThatThrownBy(() -> comment.edit(OTHER_USER_ID, "vandalism"))
                    .isInstanceOf(IllegalCommentOperation.class)
                    .hasMessageContaining("Only the author");

            assertThat(comment.getContent()).isEqualTo("the original text");
        }

        @Test
        @DisplayName("an empty edit is rejected rather than silently bumping updatedAt")
        void refuses_empty_content() {
            Comment comment = comment();

            assertThatThrownBy(() -> comment.edit(AUTHOR_ID, null))
                    .isInstanceOf(IllegalCommentOperation.class);

            assertThat(comment.getContent()).isEqualTo("the original text");
            assertThat(comment.getUpdatedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("deleteBy")
    class Delete {

        @Test
        void the_author_can_delete_their_own_comment() {
            assertThatCode(() -> comment().deleteBy(AUTHOR_ID)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("BUG: deleting used to be attempted with the arguments swapped, so it matched nothing")
        void nobody_else_can_delete_it() {
            assertThatThrownBy(() -> comment().deleteBy(OTHER_USER_ID))
                    .isInstanceOf(IllegalCommentOperation.class)
                    .hasMessageContaining("Only the author");
        }

        @Test
        @DisplayName("acting on someone else's comment is 403, not a 404 that pretends it is missing")
        void refusing_a_non_author_is_forbidden_not_not_found() {
            assertThatThrownBy(() -> comment().deleteBy(OTHER_USER_ID))
                    .isInstanceOfSatisfying(IllegalCommentOperation.class,
                            ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.FORBIDDEN));
        }
    }
}