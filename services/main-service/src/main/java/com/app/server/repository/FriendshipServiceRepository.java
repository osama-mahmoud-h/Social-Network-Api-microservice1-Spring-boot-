package com.app.server.repository;

import com.app.server.model.AppUser;
import com.app.server.model.Friendship;
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

    @Query("SELECT f FROM Friendship f WHERE (f.user1.userId = :userId AND f.user2.userId = :friendId) OR (f.user1.userId = :friendId AND f.user2.userId = :userId)")
    Optional<Friendship> findFriendshipByTwoUsers(@Param("userId") Long userId,@Param("friendId") Long friendId);


    @Transactional
    @Modifying
    @Query("UPDATE Friendship f SET f.status = :status WHERE f.id = :id")
    void updateFriendshipStatusById(@Param("id") Long id, @Param("status") String status);

//    @Query("SELECT f.user1 FROM Friendship f WHERE (f.user2.userId = :userId OR f.user1.userId = :userId) AND f.status = 'ACCEPTED'")
//    List<AppUser> findFriendsByUserId(Long userId);
//
    @Query("SELECT f.user1 FROM Friendship f WHERE (f.user2.userId = :userId OR f.user1.userId = :userId) AND f.status = :status")
    List<AppUser> findFriendsByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);


}
