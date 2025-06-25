package com.finance.finance.service.impl;

import com.finance.finance.dto.request.CategoryRequest;
import com.finance.finance.dto.response.CategoryResponse;
import com.finance.finance.entity.Category;
import com.finance.finance.entity.User;
import com.finance.finance.exception.BadRequestException;
import com.finance.finance.exception.ResourceNotFoundException;
import com.finance.finance.repository.CategoryRepository;
import com.finance.finance.repository.UserRepository;
import com.finance.finance.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public CategoryResponse createCategory(Long userId, CategoryRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Validate category type
        if (!request.getType().equalsIgnoreCase("INCOME") && !request.getType().equalsIgnoreCase("EXPENSE")) {
            throw new BadRequestException("Category type must be either INCOME or EXPENSE");
        }

        // Check for duplicate name
        if (categoryRepository.existsByNameAndUser(request.getName(), user)) {
            throw new BadRequestException("Category with the same name already exists");
        }

        Category category = new Category();
        category.setName(request.getName());
        category.setType(request.getType().toUpperCase());
        category.setCustom(true);
        category.setUser(user);

        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    @Override
    public List<CategoryResponse> getAllCategories(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        List<Category> categories = categoryRepository.findByUserOrIsCustomFalse(user);
        return categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCategory(Long userId, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

        if (!category.isCustom()) {
            throw new BadRequestException("Default categories cannot be deleted");
        }

        if (!category.getUser().getId().equals(userId)) {
            throw new BadRequestException("You are not authorized to delete this category");
        }

        if (category.getTransactions() != null && !category.getTransactions().isEmpty()) {
            throw new BadRequestException("Cannot delete category associated with existing transactions");
        }

        categoryRepository.delete(category);
    }

    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setType(category.getType());
        response.setCustom(category.isCustom());
        return response;
    }
}
