package com.finance.finance.repository;

import com.finance.finance.entity.Category;
import com.finance.finance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Check for duplicate category name per user
    boolean existsByNameAndUser(String name, User user);

    // Find category with ownership check
    Optional<Category> findByIdAndUser_Id(Long id, Long userId);

    // List categories: user’s + default shared
    List<Category> findByUserOrIsCustomFalse(User user);

    
    // Find category by name for a specific user
    Optional<Category> findByNameAndUser(String name, User user);

    // Used for filtering categories by type and source (custom or default)
    List<Category> findByTypeAndUserOrTypeAndIsCustomFalse(String type1, User user, String type2);
}
