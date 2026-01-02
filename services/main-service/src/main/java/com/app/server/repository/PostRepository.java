package com.app.server.repository;

import com.app.server.model.Post;
import com.app.server.projection.PostDetailProjection;
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
public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findAll();

    // TODO: find posts of friends of the user.
    /**
     * Fetches recent posts with aggregate counts.
     * Uses PostDetailProjection for type-safe result mapping.
     *
     * PAGINATION FIX: This query now uses a subquery to paginate post IDs first,
     * then joins with files. This prevents pagination issues where LIMIT is applied
     * to intermediate rows before GROUP BY.
     */
    @Query(value = """
           WITH paginated_posts AS (
               SELECT
                   p.post_id,
                   p.content,
                   p.publicity,
                   p.created_at,
                   p.updated_at,
                   p.author_id
               FROM posts p
               ORDER BY p.created_at DESC, p.post_id DESC
               LIMIT :limit OFFSET :offset
           )
           SELECT
               pp.post_id AS postId,
               pp.content AS content,
               pp.publicity AS publicity,
               COUNT(DISTINCT cmnt.comment_id) AS commentsCount,
               COUNT(DISTINCT re.reaction_id) AS reactionsCount,
               pp.created_at AS createdAt,
               pp.updated_at AS updatedAt,
               json_build_object(
                   'userId', au.user_id,
                   'firstName', au.first_name,
                   'lastName', au.last_name,
                   'email', null,
                   'profilePictureUrl', null,
                   'bio', null
               )::text AS author,
               MAX(CASE WHEN re.author_id = :userId THEN re.reaction_type ELSE NULL END) AS myReactionType,
               COALESCE(
                   json_agg(
                       json_build_object(
                           'fileId', f.file_id,
                           'fileUrl', f.file_url,
                           'fileType', f.file_type,
                           'fileSize', f.file_size_in_bytes,
                           'fileName', f.file_name,
                           'fileExtension', f.file_extension
                       )
                   ) FILTER (WHERE f.file_id IS NOT NULL),
                   '[]'::json
               )::text AS files
           FROM
               paginated_posts pp
               LEFT JOIN comments cmnt ON pp.post_id = cmnt.post_id
               LEFT JOIN reactions re ON re.target_id = pp.post_id
               INNER JOIN user_profiles au ON pp.author_id = au.user_id
               LEFT JOIN post_files pf ON pp.post_id = pf.post_id
               LEFT JOIN files f ON pf.file_id = f.file_id
           GROUP BY
               pp.post_id, pp.content, pp.publicity, pp.created_at, pp.updated_at, au.user_id, au.first_name, au.last_name
           ORDER BY
               pp.created_at DESC, pp.post_id DESC
           """, nativeQuery = true)
    List<PostDetailProjection> findRecentPosts(
        @Param("userId") Long userId,
        @Param("limit") int limit,
        @Param("offset") int offset
    );


    //TODO: --
    @Query("SELECT p FROM Post p WHERE p.author.userId = :userId")
    List<Post> findUserPosts(@Param("userId") Long userId, Pageable pageable);

    //TODO: get count of comments on each post.
    // TODO: get count of reactions on each post.
    //TODO check if the user reacted the post.
    @Query("SELECT p FROM Post p WHERE p.postId = :postId")
    Optional<Post> findByPostId(Long postId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Post p WHERE p.author.userId = :userId AND p.postId = :postId")
    int deletePostById(@Param("userId") Long userId, @Param("postId") Long postId);

    @Modifying
    @Transactional
    @Query("UPDATE Post p SET p.content = :content WHERE p.author.userId = :userId AND p.postId = :postId")
    int updatePostById(@Param("userId") Long userId, @Param("postId") Long postId, @Param("content") String content);

    @Query(value = """
           SELECT
               p.post_id AS postId,
               p.content AS content,
               p.publicity AS publicity,
               COUNT(DISTINCT cmnt.comment_id) AS commentsCount,
               COUNT(DISTINCT re.reaction_id) AS reactionsCount,
               p.created_at AS createdAt,
               p.updated_at AS updatedAt,
               json_build_object(
                   'userId', au.user_id,
                   'firstName', au.first_name,
                   'lastName', au.last_name,
                   'email', null,
                   'profilePictureUrl', null,
                   'bio', null
               )::text AS author,
               MAX(CASE WHEN re.author_id = :userId THEN re.reaction_type ELSE NULL END) AS myReactionType,
               COALESCE(
                   json_agg(
                       json_build_object(
                           'fileId', f.file_id,
                           'fileUrl', f.file_url,
                           'fileType', f.file_type,
                           'fileSize', f.file_size_in_bytes,
                           'fileName', f.file_name,
                           'fileExtension', f.file_extension
                       )
                   ) FILTER (WHERE f.file_id IS NOT NULL),
                   '[]'::json
               )::text AS files
           FROM
               posts p
               LEFT JOIN comments cmnt ON p.post_id = cmnt.post_id
               LEFT JOIN reactions re ON re.target_id = p.post_id
               INNER JOIN user_profiles au ON p.author_id = au.user_id
               LEFT JOIN post_files pf ON p.post_id = pf.post_id
               LEFT JOIN files f ON pf.file_id = f.file_id
           WHERE
               p.post_id = :postId
           GROUP BY
               p.post_id, p.content, p.publicity, p.created_at, p.updated_at, au.user_id, au.first_name, au.last_name
           """, nativeQuery = true)
    Optional<PostDetailProjection> findPostDetailsById(@Param("userId") Long userId, @Param("postId") Long postId);


    @Query("""
        SELECT p FROM Post p
        JOIN FETCH p.author a
        JOIN FETCH p.files f
        WHERE p.postId IN :postIds
        """)
    List<Post> getPostsByIds(List<Long> postIds, Pageable pageable);
}
