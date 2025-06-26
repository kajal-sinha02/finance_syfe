package com.finance.finance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.finance.finance.entity.User;

import java.util.Optional;

/**
 * Repository for performing CRUD operations on User entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by their username (email)
    Optional<User> findByUsername(String username);

    // Check if a user already exists by username (email)
    boolean existsByUsername(String username);
}
