package com.finance.finance.config;

import com.finance.finance.entity.Category;
import com.finance.finance.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.List;

// seeder for default categories
@Component
public class DefaultCategorySeeder {

    private final CategoryRepository categoryRepository;

    public DefaultCategorySeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @PostConstruct
    public void seedDefaultCategories() {
        List<Category> defaults = List.of(
                new Category(null, "Salary", "INCOME", false, null),
                new Category(null, "Food", "EXPENSE", false, null),
                new Category(null, "Rent", "EXPENSE", false, null),
                new Category(null, "Transportation", "EXPENSE", false, null),
                new Category(null, "Entertainment", "EXPENSE", false, null),
                new Category(null, "Healthcare", "EXPENSE", false, null),
                new Category(null, "Utilities", "EXPENSE", false, null)
        );

        for (Category category : defaults) {
            boolean exists = categoryRepository.findByNameAndUserIsNull(category.getName()).isPresent();
            if (!exists) {
                categoryRepository.save(category);
            }
        }
    }
}
