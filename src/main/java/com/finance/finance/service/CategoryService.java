package com.finance.finance.service;

import com.finance.finance.dto.request.CategoryRequest;
import com.finance.finance.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(Long userId, CategoryRequest request);

    List<CategoryResponse> getAllCategories(Long userId);

    void deleteCategory(Long userId, Long categoryId);
}
