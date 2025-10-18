package com.app.server.model;

import com.app.server.enums.FriendshipStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "friendships", uniqueConstraints = {
        @UniqueConstraint(name = "UniqueFriendship", columnNames = {"user_id1", "user_id2"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendshipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id1", referencedColumnName = "userId", foreignKey = @ForeignKey(name = "FK_friendships_user_id1"))
    private UserProfile user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id2", referencedColumnName = "userId", foreignKey = @ForeignKey(name = "FK_friendships_user_id2"))
    private UserProfile user2;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status; // PENDING, ACCEPTED, BLOCKED

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
