package com.finance.finance.controller;

import com.finance.finance.dto.request.TransactionRequest;
import com.finance.finance.dto.request.TransactionUpdateRequest;
import com.finance.finance.dto.response.TransactionResponse;
import com.finance.finance.entity.User;
import com.finance.finance.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransactionControllerTest {

    private MockMvc mockMvc;
    private TransactionService transactionService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private User mockUser;

    @BeforeEach
    void setUp() {
        transactionService = mock(TransactionService.class);
        TransactionController controller = new TransactionController(transactionService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // Setup mock user
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("test@example.com"); // Use setUsername instead of setEmail
        mockUser.setFullName("Test User");
    }

    @Test
    void testCreateTransaction() throws Exception {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(100.0); // Use Double as per TransactionRequest
        request.setDate(LocalDate.now().toString()); // String as per TransactionRequest
        request.setCategory("Food");
        request.setDescription("Lunch");

        TransactionResponse response = new TransactionResponse();
        response.setId(1L);
        response.setAmount(BigDecimal.valueOf(100)); // BigDecimal as per TransactionResponse
        response.setCategory("Food");
        response.setType("EXPENSE");
        response.setDate(LocalDate.now().toString()); // Convert LocalDate to String
        response.setDescription("Lunch");

        when(transactionService.createTransaction(any(User.class), any(TransactionRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/transactions")
                        .sessionAttr("user", mockUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.category").value("Food"));
    }

    @Test
    void testGetTransactions() throws Exception {
        TransactionResponse response = new TransactionResponse();
        response.setId(1L);
        response.setAmount(BigDecimal.valueOf(50));
        response.setCategory("Transport");
        response.setType("EXPENSE");
        response.setDate(LocalDate.now().toString()); // Convert LocalDate to String
        response.setDescription("Bus fare");

        when(transactionService.getTransactions(eq(mockUser), any(), any(), anyString()))
                .thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/transactions")
                        .sessionAttr("user", mockUser)
                        .param("category", "Transport"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions[0].category").value("Transport"));
    }

    @Test
    void testUpdateTransaction() throws Exception {
        TransactionUpdateRequest updateRequest = new TransactionUpdateRequest();
        updateRequest.setAmount(BigDecimal.valueOf(80).doubleValue()); // Convert BigDecimal to Double
        updateRequest.setDescription("Updated");
        updateRequest.setCategory("Food");
        updateRequest.setDate(LocalDate.now().toString()); // String as per TransactionUpdateRequest

        TransactionResponse response = new TransactionResponse();
        response.setId(1L);
        response.setAmount(BigDecimal.valueOf(80));
        response.setCategory("Food");
        response.setType("EXPENSE");
        response.setDate(LocalDate.now().toString()); // Convert LocalDate to String
        response.setDescription("Updated");

        when(transactionService.updateTransaction(eq(mockUser), eq(1L), any(TransactionUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/transactions/1")
                        .sessionAttr("user", mockUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated"))
                .andExpect(jsonPath("$.amount").value(80));
    }

    @Test
    void testDeleteTransaction() throws Exception {
        doNothing().when(transactionService).deleteTransaction(mockUser, 1L);

        mockMvc.perform(delete("/api/transactions/1")
                        .sessionAttr("user", mockUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction deleted successfully"));
    }
}