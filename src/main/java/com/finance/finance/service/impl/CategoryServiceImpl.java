package com.finance.finance.service.impl;

import com.finance.finance.dto.request.CategoryRequest;
import com.finance.finance.dto.response.CategoryResponse;
import com.finance.finance.entity.Category;
import com.finance.finance.entity.User;
import com.finance.finance.repository.CategoryRepository;
import com.finance.finance.repository.TransactionRepository;
import com.finance.finance.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository; // ✅ Injected here

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               TransactionRepository transactionRepository) { // ✅ Inject via constructor
        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<CategoryResponse> getAllCategories(User user) {
        return categoryRepository.findByUserOrIsCustomFalse(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse createCategory(User user, CategoryRequest request) {
        String name = request.getName().trim();
        String type = request.getType().trim().toUpperCase();

        if (!type.equals("INCOME") && !type.equals("EXPENSE")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category type");
        }

        if (categoryRepository.existsByNameAndUser(name, user)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category already exists");
        }

        Category category = Category.builder()
                .name(name)
                .type(type)
                .isCustom(true)
                .user(user)
                .build();

        categoryRepository.save(category);
        return mapToResponse(category);
    }

    @Override
    public void deleteCategory(User user, String name) {
        Category category = categoryRepository.findByNameAndUser(name, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        if (!category.isCustom()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete default category");
        }

       boolean isUsed = transactionRepository.existsByCategoryId(category.getId());
        if (isUsed) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category is in use and cannot be deleted");
        }

        categoryRepository.delete(category);
    }
    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .name(category.getName())
                .type(category.getType())
                .isCustom(category.isCustom())
                .build();
    }
}
