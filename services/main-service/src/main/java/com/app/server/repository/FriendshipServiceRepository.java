package com.app.server.repository;

import com.app.server.enums.FriendshipStatus;
import com.app.server.model.Friendship;
import com.app.server.model.UserProfile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipServiceRepository extends JpaRepository<Friendship, Long> {

    @Query("""
        SELECT f
        FROM Friendship f
        WHERE (f.user1.userId = :userId AND f.user2.userId = :friendId)
        OR
        (f.user1.userId = :friendId AND f.user2.userId = :userId)
        """)
    Optional<Friendship> findFriendshipByTwoUsers(@Param("userId") Long userId,@Param("friendId") Long friendId);


    @Transactional
    @Modifying
    @Query("UPDATE Friendship f SET f.status = :status WHERE f.friendshipId = :id")
    void updateFriendshipStatusById(@Param("id") Long id,  @Param("status") FriendshipStatus status);


    @Query(value = """
           SELECT u.user_id as userId, u.email as email,
                  u.first_name as firstName, u.last_name as lastName,
                  NULL as profilePictureUrl
           FROM user_profiles u
           JOIN friendships f ON
             (f.user_id1 = :userId AND f.user_id2 = u.user_id) OR
             (f.user_id2 = :userId AND f.user_id1 = u.user_id)
           WHERE f.status = :status
           """,
            countQuery = """
            SELECT COUNT(*) FROM user_profiles u
            JOIN friendships f ON
              (f.user_id1 = :userId AND f.user_id2 = u.user_id) OR
              (f.user_id2 = :userId AND f.user_id1 = u.user_id)
            WHERE f.status = :status
           """, nativeQuery = true)
    List<com.app.server.projection.UserSuggestionProjection> findFriendsByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    @Query(value = "SELECT u.* FROM user_profiles u " +
            "JOIN friendships f ON " +
            "  (f.user_id1 = :userId AND f.user_id2 = u.user_id) OR " +
            "  (f.user_id2 = :userId AND f.user_id1 = u.user_id) " +
            "WHERE f.status = 'ACCEPTED'",
            countQuery = "SELECT COUNT(*) FROM friendships f " +
                    "WHERE (f.user_id1 = :userId OR f.user_id2 = :userId) " +
                    "AND f.status = 'ACCEPTED'",
            nativeQuery = true)
    List<UserProfile> findFriendsPaginated(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT CASE WHEN f.user1.userId = :userId THEN f.user2 ELSE f.user1 END " +
            "FROM Friendship f " +
            "WHERE (f.user1.userId = :userId OR f.user2.userId = :userId) " +
            "AND f.status = 'ACCEPTED' " +
            "AND (:searchTerm IS NULL OR " +
            "   (CASE WHEN f.user1.userId = :userId THEN f.user2.firstName ELSE f.user1.firstName END) " +
            "   LIKE %:searchTerm%) " +
            "ORDER BY " +
            "CASE WHEN :sortDir = 'asc' THEN " +
            "   CASE WHEN f.user1.userId = :userId THEN f.user2.firstName ELSE f.user1.firstName END " +
            "END ASC, " +
            "CASE WHEN :sortDir = 'desc' THEN " +
            "   CASE WHEN f.user1.userId = :userId THEN f.user2.firstName ELSE f.user1.firstName END " +
            "END DESC")
    List<UserProfile> findFriendsWithFilter(
            @Param("userId") Long userId,
            @Param("searchTerm") String searchTerm,
            @Param("sortDir") String sortDirection);

    @Query(value = "SELECT COUNT(*) FROM (" +
            "    SELECT user_id2 AS friend_id FROM friendships " +
            "    WHERE user_id1 = :userId1 AND status = 'ACCEPTED' " +
            "    UNION ALL " +
            "    SELECT user_id1 AS friend_id FROM friendships " +
            "    WHERE user_id2 = :userId1 AND status = 'ACCEPTED'" +
            ") AS user1_friends " +
            "JOIN (" +
            "    SELECT user_id2 AS friend_id FROM friendships " +
            "    WHERE user_id1 = :userId2 AND status = 'ACCEPTED' " +
            "    UNION ALL " +
            "    SELECT user_id1 AS friend_id FROM friendships " +
            "    WHERE user_id2 = :userId2 AND status = 'ACCEPTED'" +
            ") AS user2_friends ON user1_friends.friend_id = user2_friends.friend_id",
            nativeQuery = true)
    int getCountOfMutualFriends(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT u FROM UserProfile u WHERE u.userId IN (" +
            "    SELECT CASE WHEN f1.user1.userId = :userId1 THEN f1.user2.userId ELSE f1.user1.userId END " +
            "    FROM Friendship f1 " +
            "    WHERE (f1.user1.userId = :userId1 OR f1.user2.userId = :userId1) AND f1.status = 'ACCEPTED'" +
            ") AND u.userId IN (" +
            "    SELECT CASE WHEN f2.user1.userId = :userId2 THEN f2.user2.userId ELSE f2.user1.userId END " +
            "    FROM Friendship f2 " +
            "    WHERE (f2.user1.userId = :userId2 OR f2.user2.userId = :userId2) AND f2.status = 'ACCEPTED'" +
            ")")
    List<UserProfile> findMutualFriends(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query(value = """
    WITH
    -- Get all direct friends
    direct_friends AS (
        SELECT CASE
            WHEN user_id1 = :userId THEN user_id2
            WHEN user_id2 = :userId THEN user_id1
        END AS friend_id
        FROM friendships
        WHERE (user_id1 = :userId OR user_id2 = :userId)
        AND status = 'ACCEPTED'
    ),

    -- Get friends of friends (excluding self)
    friends_of_friends AS (
        SELECT DISTINCT
            CASE
                WHEN f.user_id1 IN (SELECT friend_id FROM direct_friends) THEN f.user_id2
                WHEN f.user_id2 IN (SELECT friend_id FROM direct_friends) THEN f.user_id1
            END AS potential_friend_id
        FROM friendships f
        WHERE (f.user_id1 IN (SELECT friend_id FROM direct_friends) OR
              f.user_id2 IN (SELECT friend_id FROM direct_friends))
        AND f.status = 'ACCEPTED'
        AND CASE
            WHEN f.user_id1 IN (SELECT friend_id FROM direct_friends) THEN f.user_id2
            WHEN f.user_id2 IN (SELECT friend_id FROM direct_friends) THEN f.user_id1
        END != :userId
    )

    -- Final selection with all conditions
    SELECT u.user_id as userId, u.email as email,
           u.first_name as firstName, u.last_name as lastName,
           NULL as profilePictureUrl
    FROM user_profiles u
    JOIN friends_of_friends fof ON u.user_id = fof.potential_friend_id
    WHERE NOT EXISTS (
        SELECT 1 FROM friendships f
        WHERE (f.user_id1 = :userId AND f.user_id2 = u.user_id) OR
              (f.user_id2 = :userId AND f.user_id1 = u.user_id)
    )
    LIMIT 10
    """, nativeQuery = true)
    List<com.app.server.projection.UserSuggestionProjection> findFriendSuggestions(@Param("userId") Long userId);

    /**
     * Get list of friend IDs for a user (only accepted friendships)
     * Used by notification service to determine who to notify
     *
     * @param userId User ID
     * @return List of friend IDs
     */
    @Query(value = """
        SELECT CASE
            WHEN user_id1 = :userId THEN user_id2
            WHEN user_id2 = :userId THEN user_id1
        END AS friend_id
        FROM friendships
        WHERE (user_id1 = :userId OR user_id2 = :userId)
        AND status = 'ACCEPTED'
        """, nativeQuery = true)
    List<Long> findAcceptedFriendIds(@Param("userId") Long userId);
}
