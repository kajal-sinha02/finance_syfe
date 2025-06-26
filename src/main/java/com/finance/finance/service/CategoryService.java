    package com.finance.finance.service;

import com.finance.finance.dto.request.CategoryRequest;
import com.finance.finance.dto.response.CategoryResponse;
import com.finance.finance.entity.User;

import java.util.List;

// interface for category service
public interface CategoryService {
    // get all categories
    List<CategoryResponse> getAllCategories(User user);
    // create categories
    CategoryResponse createCategory(User user, CategoryRequest request);
    //delete categories
    void deleteCategory(User user, String name);
}
