package com.finance.finance.controller;

import com.finance.finance.dto.request.CategoryRequest;
import com.finance.finance.dto.response.CategoryResponse;
import com.finance.finance.entity.User;
import com.finance.finance.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryControllerTest {

    private MockMvc mockMvc;
    private CategoryService categoryService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private User mockUser;

    @BeforeEach
    void setUp() {
        // Configure ObjectMapper for potential date serialization
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        categoryService = mock(CategoryService.class);
        CategoryController controller = new CategoryController(categoryService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // Setup mock user for session management
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("test@example.com");
        mockUser.setFullName("Test User");
    }

    @Test
    void testGetAllCategories() throws Exception {
        CategoryResponse response = CategoryResponse.builder()
                .name("Salary")
                .type("income")
                .build();

        when(categoryService.getAllCategories(eq(mockUser)))
                .thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/categories")
                        .sessionAttr("user", mockUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories[0].name").value("Salary"))
                .andExpect(jsonPath("$.categories[0].type").value("income"));
    }

    @Test
    void testCreateCategory() throws Exception {
        CategoryRequest request = new CategoryRequest();
        request.setName("Rent");
        request.setType("expense");

        CategoryResponse response = CategoryResponse.builder()
                .name("Rent")
                .type("expense")
                .build();

        when(categoryService.createCategory(eq(mockUser), any(CategoryRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/categories")
                        .sessionAttr("user", mockUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Rent"))
                .andExpect(jsonPath("$.type").value("expense"));
    }

    @Test
    void testDeleteCategory() throws Exception {
        doNothing().when(categoryService).deleteCategory(eq(mockUser), eq("Food"));

        mockMvc.perform(delete("/api/categories/Food")
                        .sessionAttr("user", mockUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Category deleted successfully"));
    }
}