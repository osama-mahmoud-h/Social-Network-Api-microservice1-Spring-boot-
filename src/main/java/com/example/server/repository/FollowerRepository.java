//package com.example.server.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface FollowerRepository extends JpaRepository<Follower,Long> {
//
//    @Modifying
//    @Query(value = "DELETE FROM followers WHERE follower_id =?1 AND followed_id =?2" ,nativeQuery = true)
//    void unFollow(Long follower_id , Long followed_id);
//
//    @Query(value = "SELECT id FROM followers WHERE follower_id =?1 AND followed_id =?2" ,nativeQuery=true)
//    Long isFollow(Long follower_id , Long followed_id);
//}
