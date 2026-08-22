package com.app.server.model;

import com.app.server.enums.FriendshipStatus;
import com.app.server.exception.IllegalFriendshipTransition;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

/**
 * A friendship between two users, and the state machine that governs it.
 *
 * <pre>
 *   (none) --request--> PENDING --accept(addressee)--> ACCEPTED --block(either)--> BLOCKED
 *                          |                                                         |
 *                   cancel(requester)                                    unblock(blocker) --> ACCEPTED
 *                          v
 *                       (deleted)
 * </pre>
 *
 * Every transition asserts both the <em>current status</em> and the <em>actor's role</em>.
 * Direction matters: the requester sent the request, the addressee received it — which is
 * why they are named that way rather than {@code user1}/{@code user2}.
 */
@Entity
@Table(name = "friendships", uniqueConstraints = {
        @UniqueConstraint(name = "UniqueFriendship", columnNames = {"requester_id", "addressee_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friendship_id")
    private Long friendshipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(name = "FK_friendships_user_id1", foreignKeyDefinition = "FOREIGN KEY (requester_id) REFERENCES user_profiles(user_id) ON DELETE CASCADE"))
    private UserProfile requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addressee_id", referencedColumnName = "user_id", foreignKey = @ForeignKey(name = "FK_friendships_user_id2", foreignKeyDefinition = "FOREIGN KEY (addressee_id) REFERENCES user_profiles(user_id) ON DELETE CASCADE"))
    private UserProfile addressee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status;

    /**
     * Who blocked. Null unless {@link FriendshipStatus#BLOCKED}, and also null for rows
     * blocked before this column existed — see {@link #unblock(Long)}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_by", referencedColumnName = "user_id", foreignKey = @ForeignKey(name = "FK_friendships_blocked_by"))
    private UserProfile blockedBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /** Opens a friendship in PENDING. The sender is the requester by construction. */
    public static Friendship request(UserProfile requester, UserProfile addressee) {
        Objects.requireNonNull(requester, "requester");
        Objects.requireNonNull(addressee, "addressee");
        if (requester.getUserId().equals(addressee.getUserId())) {
            throw new IllegalFriendshipTransition("You cannot send a friend request to yourself");
        }
        Friendship friendship = new Friendship();
        friendship.requester = requester;
        friendship.addressee = addressee;
        friendship.status = FriendshipStatus.PENDING;
        friendship.createdAt = Instant.now();
        return friendship;
    }

    /** Only the addressee can accept, and only while the request is still pending. */
    public void accept(Long actorId) {
        requireStatus(FriendshipStatus.PENDING, "accept");
        if (!isAddressee(actorId)) {
            throw new IllegalFriendshipTransition("Only the person who received the request can accept it");
        }
        this.status = FriendshipStatus.ACCEPTED;
    }

    /** Either participant can block an accepted friendship. */
    public void block(Long actorId) {
        requireStatus(FriendshipStatus.ACCEPTED, "block");
        this.status = FriendshipStatus.BLOCKED;
        this.blockedBy = isRequester(actorId) ? requester : addressee;
    }

    /**
     * Only whoever blocked can unblock. Rows blocked before {@code blocked_by} existed carry no
     * blocker, so either participant may lift those — the alternative is leaving them stuck.
     */
    public void unblock(Long actorId) {
        requireStatus(FriendshipStatus.BLOCKED, "unblock");
        if (blockedBy != null && !blockedBy.getUserId().equals(actorId)) {
            throw new IllegalFriendshipTransition("Only the user who blocked can unblock");
        }
        this.status = FriendshipStatus.ACCEPTED;
        this.blockedBy = null;
    }

    /**
     * Only the requester can withdraw a request they sent, and only while it is pending.
     * Deletion of the row itself is the caller's job — an aggregate cannot delete itself.
     */
    public void cancel(Long actorId) {
        requireStatus(FriendshipStatus.PENDING, "cancel");
        if (!isRequester(actorId)) {
            throw new IllegalFriendshipTransition("Only the sender can cancel a friend request");
        }
    }

    /** Ending an established friendship. Either side may do it; deletion is the caller's job. */
    public void remove(Long actorId) {
        requireStatus(FriendshipStatus.ACCEPTED, "remove");
        requireParticipant(actorId);
    }

    public boolean isAccepted() {
        return status == FriendshipStatus.ACCEPTED;
    }

    public boolean isRequester(Long userId) {
        return requester != null && requester.getUserId().equals(userId);
    }

    public boolean isAddressee(Long userId) {
        return addressee != null && addressee.getUserId().equals(userId);
    }

    public boolean involves(Long userId) {
        return isRequester(userId) || isAddressee(userId);
    }

    /** The other party, from {@code userId}'s point of view. */
    public UserProfile counterpartOf(Long userId) {
        requireParticipant(userId);
        return isRequester(userId) ? addressee : requester;
    }

    private void requireStatus(FriendshipStatus expected, String action) {
        if (status != expected) {
            throw new IllegalFriendshipTransition(
                    "Cannot " + action + " a friendship that is " + status);
        }
    }

    private void requireParticipant(Long actorId) {
        if (!involves(actorId)) {
            throw new IllegalFriendshipTransition("You are not part of this friendship");
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Friendship that)) {
            return false;
        }
        // Identity only: two rows are the same friendship iff they share an assigned ID.
        return friendshipId != null && friendshipId.equals(that.friendshipId);
    }

    @Override
    public int hashCode() {
        return Friendship.class.hashCode();
    }
}
