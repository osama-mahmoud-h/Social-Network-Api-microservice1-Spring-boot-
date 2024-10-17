package com.example.server.repository;

import com.example.server.model.UserReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReactionRepository extends JpaRepository<UserReaction,Long> {
}
