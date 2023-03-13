package com.example.server.repository;

import com.example.server.models.Post;
import org.hibernate.query.NativeQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findAll();
  ///  @Query(value = "",nativeQuery = true)
  ///  Post likePost(Long user_id,Long post_id);
}
