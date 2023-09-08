package com.example.server.repository;

import com.example.server.models.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike,Long> {

    @Modifying
    @Query(value = "DELETE FROM PostLike where liker.id= :likerId AND post.id= :postId")
    void deleteLikeOnPost(@Param("likerId") Long likerId, @Param("postId")Long postId);

    @Modifying
    @Query(value = "delete from PostLike where id = :id")
    void deleteById(@Param("id") Long id);
    boolean existsById(Long id);
}
