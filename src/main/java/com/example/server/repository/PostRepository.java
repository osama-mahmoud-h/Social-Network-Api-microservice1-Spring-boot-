package com.example.server.repository;

import com.example.server.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findAll();

    // TODO: write jpql query.
    @Query("SELECT p FROM Post p WHERE p.author.userId = :userId")
    List<Post> findLatestPosts(@Param("userId") Long userId, Pageable pageable);

    //TODO: write jpql query.
    @Query("SELECT p FROM Post p WHERE p.author.userId = :userId")
    List<Post> findUserPosts(@Param("userId") Long userId, Pageable pageable);
}
