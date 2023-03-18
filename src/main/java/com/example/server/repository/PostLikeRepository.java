package com.example.server.repository;

import com.example.server.models.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike,Long> {

    @Modifying
    @Query(value = "DELETE FROM users_like_posts where liker_id=?1 AND post_id=?2",nativeQuery = true)
    void deleteLikeOnPost(Long liker_id,Long post_id);

    @Modifying
    @Query(value = "delete from users_like_posts where id =?1",nativeQuery = true)
    void deleteById(Long id);

    boolean existsById(Long id);
}
