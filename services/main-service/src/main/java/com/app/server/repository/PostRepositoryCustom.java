package com.app.server.repository;

import com.app.server.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Custom repository for complex Post queries
 * Using JPQL with entity fetching instead of native SQL
 */
@Repository
public interface PostRepositoryCustom {

    /**
     * Fetch post with all related data using JPQL
     * Returns actual entities, not JSON!
     *
     * @param postId Post ID
     * @return Post with eagerly loaded files, author
     */
    @Query("""
        SELECT DISTINCT p
        FROM Post p
        LEFT JOIN FETCH p.files f
        LEFT JOIN FETCH p.author a
        WHERE p.postId = :postId
        """)
    Optional<Post> findPostWithFiles(@Param("postId") Long postId);

    /**
     * Alternative: Fetch with all collections
     */
    @Query("""
        SELECT DISTINCT p
        FROM Post p
        LEFT JOIN FETCH p.files
        LEFT JOIN FETCH p.comments
        LEFT JOIN FETCH p.author
        WHERE p.postId = :postId
        """)
    Optional<Post> findPostWithAllDetails(@Param("postId") Long postId);
}