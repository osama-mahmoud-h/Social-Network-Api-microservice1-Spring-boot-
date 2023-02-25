package com.example.server.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.server.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findUserByEmail(String email);

  Optional<User> findUserById(Long id);

  //Boolean existsByUsername(String username);
  Boolean existsByEmail(String email);
}
