package com.finance.finance.repository;

import com.finance.finance.entity.Category;
import com.finance.finance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for performing CRUD operations on Category entities.
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // find by user or is custom false
    List<Category> findByUserOrIsCustomFalse(User user);

    // find by name and user
    Optional<Category> findByNameAndUser(String name, User user);

    // find by name and user is null
    Optional<Category> findByNameAndUserIsNull(String name);

    // check whether category exist by user and name
    boolean existsByNameAndUser(String name, User user);
}
