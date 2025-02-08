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
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id1", referencedColumnName = "userId", foreignKey = @ForeignKey(name = "FK_friendships_user_id1"))
    private AppUser user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id2", referencedColumnName = "userId", foreignKey = @ForeignKey(name = "FK_friendships_user_id2"))
    private AppUser user2;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status; // PENDING, ACCEPTED, BLOCKED

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
