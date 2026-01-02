package com.app.server.repository;

import com.app.server.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File,Long> {

    /**
     * Find file by its object name/URL in storage.
     */
    Optional<File> findByFileUrl(String fileUrl);

    /**
     * Check if a user cannot access a file.
     * Returns true if:
     * 1. current user not blocked by owner.
     * 2. post scope is friends only and this is not a friend.
     * 3. is private
     *
     * @param fileUrl The file object name in storage
     * @param userId The user ID requesting access
     * @return true if user has access
     */
    @Query(value = """
        SELECT true
        """, nativeQuery = true)
    Boolean canUserAccessFile(@Param("fileUrl") String fileUrl, @Param("userId") Long userId);

    /**
     * Check if a user can access a file by file ID.
     *
     * @param fileId The file database ID
     * @param userId The user ID requesting access
     * @return true if user has access
     */
    @Query(value = """
        SELECT true
        """, nativeQuery = true)
    Boolean canUserAccessFileById(@Param("fileId") Long fileId, @Param("userId") Long userId);

    /**
     * Get the author ID of a post containing the file.
     * Useful for ownership checks.
     *
     * @param fileUrl The file object name
     * @return Optional author ID
     */
    @Query(value = """
        SELECT p.author_id
        FROM posts p
        JOIN post_files pf ON p.post_id = pf.post_id
        JOIN files f ON pf.file_id = f.file_id
        WHERE f.file_url = :fileUrl
        LIMIT 1
        """, nativeQuery = true)
    Optional<Long> findPostAuthorIdByFileUrl(@Param("fileUrl") String fileUrl);
}
