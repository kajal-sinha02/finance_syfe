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
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
public CategoryResponse getCategoryById(Long categoryId, Principal principal) {
    User user = getCurrentUser(principal);
    
    Category category = categoryRepository.findById(categoryId)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

    if (!category.isCustom() && category.getUser() == null) {
        // Allow access to default categories (which have no user)
        return mapToResponse(category);
    }

    if (!category.getUser().getId().equals(user.getId())) {
        throw new BadRequestException("You are not authorized to view this category");
    }

    return mapToResponse(category);
}

    @Override
    public CategoryResponse createCategory(CategoryRequest request, Principal principal) {
        User user = getCurrentUser(principal);

        validateCategoryType(request.getType());

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
public CategoryResponse updateCategory(Long categoryId, CategoryRequest request, Principal principal) {
    User user = getCurrentUser(principal);

    Category category = categoryRepository.findByIdAndUser_Id(categoryId, user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    if (!category.isCustom()) {
        throw new BadRequestException("Default categories cannot be updated");
    }

    validateCategoryType(request.getType());

    if (categoryRepository.existsByNameAndUser(request.getName(), user) &&
            !category.getName().equalsIgnoreCase(request.getName())) {
        throw new BadRequestException("Another category with the same name already exists");
    }

    category.setName(request.getName());
    category.setType(request.getType().toUpperCase());

    Category updated = categoryRepository.save(category);
    return mapToResponse(updated);
}

    @Override
    public List<CategoryResponse> getAllCategories(Principal principal) {
        User user = getCurrentUser(principal);
        List<Category> categories = categoryRepository.findByUserOrIsCustomFalse(user);
        return categories.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

 @Override
public void deleteCategory(Long categoryId, Principal principal) {
    User user = getCurrentUser(principal);
    Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));

    if (!category.isCustom()) {
        throw new BadRequestException("Default categories cannot be deleted");
    }

    if (!category.getUser().getId().equals(user.getId())) {
        throw new BadRequestException("You are not authorized to delete this category");
    }

    if (category.getTransactions() != null && !category.getTransactions().isEmpty()) {
        throw new BadRequestException("Cannot delete category associated with existing transactions");
    }

    categoryRepository.delete(category);
}

    private void validateCategoryType(String type) {
        if (!type.equalsIgnoreCase("INCOME") && !type.equalsIgnoreCase("EXPENSE")) {
            throw new BadRequestException("Category type must be either INCOME or EXPENSE");
        }
    }

    private CategoryResponse mapToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setType(category.getType());
        response.setCustom(category.isCustom());
        return response;
    }

    private User getCurrentUser(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + principal.getName()));
    }
    @Override
public void deleteCategoryByName(String name, Principal principal) {
    User user = getCurrentUser(principal);

    Category category = categoryRepository.findByNameAndUser(name, user)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found with name: " + name));

    if (!category.isCustom()) {
        throw new BadRequestException("Default categories cannot be deleted");
    }

    if (category.getTransactions() != null && !category.getTransactions().isEmpty()) {
        throw new BadRequestException("Cannot delete category associated with existing transactions");
    }

    categoryRepository.delete(category);
}
}
