package com.finance.finance.repository;

import com.finance.finance.entity.Category;
import com.finance.finance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserOrIsCustomFalse(User user);
   Optional<Category> findByNameAndUser(String name, User user);
    Optional<Category> findByNameAndUserIsNull(String name);
    boolean existsByNameAndUser(String name, User user);
}
