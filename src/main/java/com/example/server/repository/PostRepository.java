package com.example.server.repository;

import com.example.server.dto.response.PostResponseDto;
import com.example.server.model.Post;
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
    // TODO: get count of comments on each post.
    // TODO: get count of reactions on each post.
    //TODO check if the user reacted the post.
    @Query(value = """
           SELECT 
               p.post_id AS postId,
               p.content AS content,
               COUNT(DISTINCT cmnt.comment_id) AS commentsCount,
               COUNT(DISTINCT pre.reaction_id) AS reactionsCount,
               p.created_at AS createdAt,
               p.updated_at AS updatedAt,
               json_build_object(
                   'userId', au.user_id,
                   'firstName', au.first_name,
                   'lastName', au.last_name,
                    'email', null,
                   'profilePictureUrl', null,
                    'bio', null
               ) AS author,
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
               ) AS files
           FROM 
               posts p
               LEFT JOIN comments cmnt ON p.post_id = cmnt.post_id
               LEFT JOIN post_reactions pre ON p.post_id = pre.post_id
               LEFT JOIN reactions re ON pre.reaction_id = re.reaction_id
               LEFT JOIN users au ON p.author_id = au.user_id
               LEFT JOIN post_files pf ON p.post_id = pf.post_id
               LEFT JOIN files f ON pf.file_id = f.file_id
           GROUP BY 
               p.post_id, au.user_id, p.created_at
           ORDER BY 
               p.post_id, p.created_at DESC
           """, nativeQuery = true)
        List<Object[]> findRecentPosts(@Param("userId") Long userId, Pageable pageable);


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
               COUNT(DISTINCT cmnt.comment_id) AS commentsCount,
               COUNT(DISTINCT pre.reaction_id) AS reactionsCount,
               p.created_at AS createdAt,
               p.updated_at AS updatedAt,
               json_build_object(
                   'userId', au.user_id,
                   'firstName', au.first_name,
                   'lastName', au.last_name,
                    'email', null,
                   'profilePictureUrl', null,
                    'bio', null
               ) AS author,
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
               ) AS files
           FROM 
               posts p
               LEFT JOIN comments cmnt ON p.post_id = cmnt.post_id
               LEFT JOIN post_reactions pre ON p.post_id = pre.post_id
               LEFT JOIN reactions re ON pre.reaction_id = re.reaction_id
               LEFT JOIN users au ON p.author_id = au.user_id
               LEFT JOIN post_files pf ON p.post_id = pf.post_id
               LEFT JOIN files f ON pf.file_id = f.file_id
           WHERE 
               p.post_id = :postId
           GROUP BY 
               p.post_id, au.user_id
           """, nativeQuery = true)
    Optional<Object> findPostDetailsById(@Param("userId") Long userId, @Param("postId") Long postId);
}
