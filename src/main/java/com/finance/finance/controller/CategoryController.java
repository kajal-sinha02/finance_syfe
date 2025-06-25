package com.finance.finance.controller;

import com.finance.finance.dto.request.CategoryRequest;
import com.finance.finance.dto.response.CategoryResponse;
import com.finance.finance.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestParam Long userId,
            @RequestBody CategoryRequest request
    ) {
        CategoryResponse response = categoryService.createCategory(userId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(@RequestParam Long userId) {
        List<CategoryResponse> categories = categoryService.getAllCategories(userId);
        return ResponseEntity.ok(categories);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @RequestParam Long userId,
            @PathVariable Long id
    ) {
        categoryService.deleteCategory(userId, id);
        return ResponseEntity.noContent().build();
    }
}
