package com.finance.finance.service;

import com.finance.finance.dto.request.CategoryRequest;
import com.finance.finance.dto.response.CategoryResponse;

import java.security.Principal;
import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryRequest request, Principal principal);

    List<CategoryResponse> getAllCategories(Principal principal);

    void deleteCategory(Long categoryId, Principal principal);

    CategoryResponse updateCategory(Long categoryId, CategoryRequest request, Principal principal);

    // Optional: If you plan to fetch a category by ID
    CategoryResponse getCategoryById(Long categoryId, Principal principal);

    void deleteCategoryByName(String name, Principal principal);


}
