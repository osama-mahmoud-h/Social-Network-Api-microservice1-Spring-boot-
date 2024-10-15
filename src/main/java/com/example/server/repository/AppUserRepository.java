package com.example.server.repository;

import java.util.Optional;

import com.example.server.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

  Optional<AppUser> findUserByEmail(String email);

  Optional<AppUser> findUserByUserId(Long id);

  //Boolean existsByUsername(String username);
  Boolean existsByEmail(String email);

}
