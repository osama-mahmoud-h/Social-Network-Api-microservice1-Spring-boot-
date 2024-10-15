package com.example.server.repository;

import com.example.server.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findAll();
  ///  @Query(value = "",nativeQuery = true)
  ///  Post likePost(Long user_id,Long post_id);
}
