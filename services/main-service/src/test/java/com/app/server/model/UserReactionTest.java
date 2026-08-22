package com.app.server.model;

import com.app.server.enums.ReactionTargetType;
import com.app.server.enums.ReactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The reaction aggregate. Pure unit test — no Spring, no database.
 */
class UserReactionTest {

    private static final Long AUTHOR_ID = 1L;
    private static final Long TARGET_ID = 42L;

    private static UserProfile author() {
        UserProfile user = new UserProfile();
        user.setUserId(AUTHOR_ID);
        return user;
    }

    @Nested
    @DisplayName("creation")
    class Creation {

        @Test
        void a_post_reaction_carries_the_post_as_its_target() {
            UserReaction reaction = UserReaction.onPost(author(), TARGET_ID, ReactionType.LIKE);

            assertThat(reaction.getReactionTargetType()).isEqualTo(ReactionTargetType.POST);
            assertThat(reaction.getTargetId()).isEqualTo(TARGET_ID);
            assertThat(reaction.getReactionType()).isEqualTo(ReactionType.LIKE);
            assertThat(reaction.isBy(AUTHOR_ID)).isTrue();
        }

        @Test
        void a_comment_reaction_carries_the_comment_as_its_target() {
            UserReaction reaction = UserReaction.onComment(author(), TARGET_ID, ReactionType.LIKE);

            assertThat(reaction.getReactionTargetType()).isEqualTo(ReactionTargetType.COMMENT);
            assertThat(reaction.getTargetId()).isEqualTo(TARGET_ID);
        }

        @Test
        void a_reaction_needs_a_target() {
            assertThatThrownBy(() -> UserReaction.onPost(author(), null, ReactionType.LIKE))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("targetId");
        }

        @Test
        void a_reaction_needs_an_author() {
            assertThatThrownBy(() -> UserReaction.onPost(null, TARGET_ID, ReactionType.LIKE))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("author");
        }
    }

    @Nested
    @DisplayName("changeTo")
    class ChangeTo {

        @Test
        void swaps_one_reaction_for_another() {
            UserReaction reaction = UserReaction.onPost(author(), TARGET_ID, ReactionType.LIKE);

            reaction.changeTo(ReactionType.LOVE);

            assertThat(reaction.getReactionType()).isEqualTo(ReactionType.LOVE);
            assertThat(reaction.is(ReactionType.LOVE)).isTrue();
            assertThat(reaction.is(ReactionType.LIKE)).isFalse();
        }

        @Test
        @DisplayName("re-applying the same reaction is a toggle-off, not a change")
        void refuses_the_reaction_that_is_already_set() {
            UserReaction reaction = UserReaction.onPost(author(), TARGET_ID, ReactionType.LIKE);

            assertThatThrownBy(() -> reaction.changeTo(ReactionType.LIKE))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("already");
        }
    }
}