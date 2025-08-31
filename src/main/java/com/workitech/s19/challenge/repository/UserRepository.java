package com.workitech.s19.challenge.repository;

import com.workitech.s19.challenge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findUserByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findUserByUsername(String username);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
