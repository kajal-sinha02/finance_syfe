package com.finance.finance.repository;

import com.finance.finance.entity.Category;
import com.finance.finance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findByNameAndUser(String name, User user);

    List<Category> findByUserOrIsCustomFalse(User user);

    boolean existsByNameAndUser(String name, User user);

    List<Category> findByTypeAndUserOrTypeAndIsCustomFalse(String type1, User user, String type2);
}
