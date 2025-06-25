package com.finance.finance.controller;

import com.finance.finance.dto.request.CategoryRequest;
import com.finance.finance.dto.response.CategoryResponse;
import com.finance.finance.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestBody CategoryRequest request,
            Principal principal
    ) {
        CategoryResponse response = categoryService.createCategory(request, principal);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(Principal principal) {
        List<CategoryResponse> categories = categoryService.getAllCategories(principal);
        return ResponseEntity.ok(categories);
    }

  // Delete by ID
@DeleteMapping("/id/{id}")
public ResponseEntity<Void> deleteCategory(
        @PathVariable Long id,
        Principal principal
) {
    categoryService.deleteCategory(id, principal);
    return ResponseEntity.noContent().build();
}

// Delete by name
@DeleteMapping("/{name}")
public ResponseEntity<Map<String, String>> deleteCategoryByName(
        @PathVariable String name,
        Principal principal
) {
    categoryService.deleteCategoryByName(name, principal);

    Map<String, String> response = new HashMap<>();
    response.put("message", "Category deleted successfully");

    return ResponseEntity.ok(response);
}

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest request,
            Principal principal
    ) {
        CategoryResponse updatedCategory = categoryService.updateCategory(id, request, principal);
        return ResponseEntity.ok(updatedCategory);
    }
}