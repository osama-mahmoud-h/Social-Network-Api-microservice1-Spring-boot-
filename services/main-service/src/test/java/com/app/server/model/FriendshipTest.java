package com.app.server.model;

import com.app.server.enums.FriendshipStatus;
import com.app.server.exception.IllegalFriendshipTransition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * The friendship transition table, asserted end to end. Pure unit test — the aggregate has no
 * dependency on JPA, Spring or a database, which is the point of putting the rules here.
 */
class FriendshipTest {

    private static final Long REQUESTER_ID = 1L;
    private static final Long ADDRESSEE_ID = 2L;
    private static final Long OUTSIDER_ID = 99L;

    private static UserProfile user(Long id) {
        UserProfile user = new UserProfile();
        user.setUserId(id);
        return user;
    }

    private static Friendship pending() {
        return Friendship.request(user(REQUESTER_ID), user(ADDRESSEE_ID));
    }

    private static Friendship accepted() {
        Friendship friendship = pending();
        friendship.accept(ADDRESSEE_ID);
        return friendship;
    }

    private static Friendship blockedBy(Long actorId) {
        Friendship friendship = accepted();
        friendship.block(actorId);
        return friendship;
    }

    @Nested
    @DisplayName("request")
    class Request {

        @Test
        void opens_in_pending_with_the_sender_as_requester() {
            Friendship friendship = pending();

            assertThat(friendship.getStatus()).isEqualTo(FriendshipStatus.PENDING);
            assertThat(friendship.isRequester(REQUESTER_ID)).isTrue();
            assertThat(friendship.isAddressee(ADDRESSEE_ID)).isTrue();
            assertThat(friendship.getBlockedBy()).isNull();
            assertThat(friendship.getCreatedAt()).isNotNull();
        }

        @Test
        void cannot_be_sent_to_yourself() {
            UserProfile self = user(REQUESTER_ID);

            assertThatThrownBy(() -> Friendship.request(self, user(REQUESTER_ID)))
                    .isInstanceOf(IllegalFriendshipTransition.class)
                    .hasMessageContaining("yourself");
        }
    }

    @Nested
    @DisplayName("accept")
    class Accept {

        @Test
        void addressee_accepts_a_pending_request() {
            Friendship friendship = pending();

            friendship.accept(ADDRESSEE_ID);

            assertThat(friendship.getStatus()).isEqualTo(FriendshipStatus.ACCEPTED);
        }

        @Test
        @DisplayName("BUG 2: the requester cannot accept their own request")
        void requester_cannot_accept_their_own_request() {
            Friendship friendship = pending();

            assertThatThrownBy(() -> friendship.accept(REQUESTER_ID))
                    .isInstanceOf(IllegalFriendshipTransition.class)
                    .hasMessageContaining("received the request");

            assertThat(friendship.getStatus()).isEqualTo(FriendshipStatus.PENDING);
        }

        @Test
        void an_outsider_cannot_accept() {
            assertThatThrownBy(() -> pending().accept(OUTSIDER_ID))
                    .isInstanceOf(IllegalFriendshipTransition.class);
        }

        @Test
        void an_already_accepted_friendship_cannot_be_accepted_again() {
            assertThatThrownBy(() -> accepted().accept(ADDRESSEE_ID))
                    .isInstanceOf(IllegalFriendshipTransition.class)
                    .hasMessageContaining("ACCEPTED");
        }

        @Test
        void a_blocked_friendship_cannot_be_accepted() {
            assertThatThrownBy(() -> blockedBy(REQUESTER_ID).accept(ADDRESSEE_ID))
                    .isInstanceOf(IllegalFriendshipTransition.class)
                    .hasMessageContaining("BLOCKED");
        }
    }

    @Nested
    @DisplayName("cancel")
    class Cancel {

        @Test
        void the_requester_can_withdraw_a_pending_request() {
            assertThatCode(() -> pending().cancel(REQUESTER_ID)).doesNotThrowAnyException();
        }

        @Test
        void the_addressee_cannot_cancel_it() {
            assertThatThrownBy(() -> pending().cancel(ADDRESSEE_ID))
                    .isInstanceOf(IllegalFriendshipTransition.class)
                    .hasMessageContaining("sender");
        }

        @Test
        void an_accepted_friendship_cannot_be_cancelled() {
            assertThatThrownBy(() -> accepted().cancel(REQUESTER_ID))
                    .isInstanceOf(IllegalFriendshipTransition.class)
                    .hasMessageContaining("ACCEPTED");
        }
    }

    @Nested
    @DisplayName("block")
    class Block {

        @Test
        void either_participant_can_block_an_accepted_friendship() {
            Friendship blockedByRequester = blockedBy(REQUESTER_ID);
            Friendship blockedByAddressee = blockedBy(ADDRESSEE_ID);

            assertThat(blockedByRequester.getStatus()).isEqualTo(FriendshipStatus.BLOCKED);
            assertThat(blockedByRequester.getBlockedBy().getUserId()).isEqualTo(REQUESTER_ID);
            assertThat(blockedByAddressee.getBlockedBy().getUserId()).isEqualTo(ADDRESSEE_ID);
        }

        @Test
        void a_pending_request_cannot_be_blocked() {
            assertThatThrownBy(() -> pending().block(REQUESTER_ID))
                    .isInstanceOf(IllegalFriendshipTransition.class)
                    .hasMessageContaining("PENDING");
        }
    }

    @Nested
    @DisplayName("unblock")
    class Unblock {

        @Test
        @DisplayName("BUG 1: unblocking a blocked friendship actually succeeds")
        void the_blocker_can_lift_their_own_block() {
            Friendship friendship = blockedBy(REQUESTER_ID);

            friendship.unblock(REQUESTER_ID);

            assertThat(friendship.getStatus()).isEqualTo(FriendshipStatus.ACCEPTED);
            assertThat(friendship.getBlockedBy()).isNull();
        }

        @Test
        void the_blocked_party_cannot_lift_the_block() {
            Friendship friendship = blockedBy(REQUESTER_ID);

            assertThatThrownBy(() -> friendship.unblock(ADDRESSEE_ID))
                    .isInstanceOf(IllegalFriendshipTransition.class)
                    .hasMessageContaining("who blocked");

            assertThat(friendship.getStatus()).isEqualTo(FriendshipStatus.BLOCKED);
        }

        @Test
        void an_accepted_friendship_cannot_be_unblocked() {
            assertThatThrownBy(() -> accepted().unblock(REQUESTER_ID))
                    .isInstanceOf(IllegalFriendshipTransition.class)
                    .hasMessageContaining("ACCEPTED");
        }
    }

    @Nested
    @DisplayName("remove")
    class Remove {

        @Test
        void either_participant_can_end_an_accepted_friendship() {
            assertThatCode(() -> accepted().remove(REQUESTER_ID)).doesNotThrowAnyException();
            assertThatCode(() -> accepted().remove(ADDRESSEE_ID)).doesNotThrowAnyException();
        }

        @Test
        void an_outsider_cannot() {
            assertThatThrownBy(() -> accepted().remove(OUTSIDER_ID))
                    .isInstanceOf(IllegalFriendshipTransition.class)
                    .hasMessageContaining("not part of");
        }

        @Test
        void a_pending_request_is_cancelled_not_removed() {
            assertThatThrownBy(() -> pending().remove(REQUESTER_ID))
                    .isInstanceOf(IllegalFriendshipTransition.class)
                    .hasMessageContaining("PENDING");
        }
    }

    @Nested
    @DisplayName("counterpartOf")
    class Counterpart {

        @Test
        void returns_the_other_participant() {
            Friendship friendship = accepted();

            assertThat(friendship.counterpartOf(REQUESTER_ID).getUserId()).isEqualTo(ADDRESSEE_ID);
            assertThat(friendship.counterpartOf(ADDRESSEE_ID).getUserId()).isEqualTo(REQUESTER_ID);
        }

        @Test
        void refuses_a_user_who_is_not_in_the_friendship() {
            assertThatThrownBy(() -> accepted().counterpartOf(OUTSIDER_ID))
                    .isInstanceOf(IllegalFriendshipTransition.class);
        }
    }
}