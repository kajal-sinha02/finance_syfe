    package com.finance.finance.service;

import com.finance.finance.dto.request.CategoryRequest;
import com.finance.finance.dto.response.CategoryResponse;
import com.finance.finance.entity.User;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories(User user);
    CategoryResponse createCategory(User user, CategoryRequest request);
    void deleteCategory(User user, String name);
}
