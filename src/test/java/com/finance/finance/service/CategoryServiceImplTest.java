package com.finance.finance.service;

import com.finance.finance.dto.request.CategoryRequest;
import com.finance.finance.dto.response.CategoryResponse;
import com.finance.finance.entity.Category;
import com.finance.finance.entity.User;
import com.finance.finance.repository.CategoryRepository;
import com.finance.finance.repository.TransactionRepository;
import com.finance.finance.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }

    @Test
    void getAllCategories_ReturnsMappedCategories() {
        Category defaultCategory = Category.builder()
                .name("Food")
                .type("EXPENSE")
                .isCustom(false)
                .user(null)
                .build();

        Category customCategory = Category.builder()
                .name("Freelance")
                .type("INCOME")
                .isCustom(true)
                .user(testUser)
                .build();

        when(categoryRepository.findByUserOrIsCustomFalse(testUser))
                .thenReturn(Arrays.asList(defaultCategory, customCategory));

        List<CategoryResponse> responses = categoryService.getAllCategories(testUser);

        assertEquals(2, responses.size());
        assertTrue(responses.stream().anyMatch(c -> c.getName().equals("Food") && !c.isCustom()));
        assertTrue(responses.stream().anyMatch(c -> c.getName().equals("Freelance") && c.isCustom()));

        verify(categoryRepository).findByUserOrIsCustomFalse(testUser);
    }

    @Test
    void createCategory_ValidRequest_Success() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Investments");
        request.setType("INCOME");

        when(categoryRepository.existsByNameAndUser("Investments", testUser)).thenReturn(false);

        Category savedCategory = Category.builder()
                .name("Investments")
                .type("INCOME")
                .isCustom(true)
                .user(testUser)
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        CategoryResponse response = categoryService.createCategory(testUser, request);

        assertEquals("Investments", response.getName());
        assertEquals("INCOME", response.getType());
        assertTrue(response.isCustom());

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void createCategory_InvalidType_ThrowsException() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Random");
        request.setType("OTHER");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> categoryService.createCategory(testUser, request));

        assertEquals("Invalid category type", ex.getReason());
    }

    @Test
    void createCategory_DuplicateName_ThrowsException() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Food");
        request.setType("EXPENSE");

        when(categoryRepository.existsByNameAndUser("Food", testUser)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> categoryService.createCategory(testUser, request));

        assertEquals("Category already exists", ex.getReason());
    }

    @Test
    void deleteCategory_CustomAndUnused_Success() {
        Category category = Category.builder()
                .id(1L)
                .name("Temporary")
                .type("EXPENSE")
                .isCustom(true)
                .user(testUser)
                .build();

        when(categoryRepository.findByNameAndUser("Temporary", testUser)).thenReturn(Optional.of(category));
        when(transactionRepository.existsByCategoryId(1L)).thenReturn(false);

        categoryService.deleteCategory(testUser, "Temporary");

        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_NotFound_ThrowsException() {
        when(categoryRepository.findByNameAndUser("Unknown", testUser)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> categoryService.deleteCategory(testUser, "Unknown"));

        assertEquals("Category not found", ex.getReason());
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void deleteCategory_DefaultCategory_ThrowsForbidden() {
        Category category = Category.builder()
                .id(2L)
                .name("Food")
                .type("EXPENSE")
                .isCustom(false)
                .user(null)
                .build();

        when(categoryRepository.findByNameAndUser("Food", testUser)).thenReturn(Optional.of(category));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> categoryService.deleteCategory(testUser, "Food"));

        assertEquals("Cannot delete default category", ex.getReason());
    }

    @Test
    void deleteCategory_UsedCategory_ThrowsException() {
        Category category = Category.builder()
                .id(3L)
                .name("Rent")
                .type("EXPENSE")
                .isCustom(true)
                .user(testUser)
                .build();

        when(categoryRepository.findByNameAndUser("Rent", testUser)).thenReturn(Optional.of(category));
        when(transactionRepository.existsByCategoryId(3L)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> categoryService.deleteCategory(testUser, "Rent"));

        assertEquals("Category is in use and cannot be deleted", ex.getReason());
    }
}
