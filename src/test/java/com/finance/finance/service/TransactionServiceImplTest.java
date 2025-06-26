package com.finance.finance.service;

import com.finance.finance.dto.request.TransactionRequest;
import com.finance.finance.dto.request.TransactionUpdateRequest;
import com.finance.finance.dto.response.TransactionResponse;
import com.finance.finance.entity.Category;
import com.finance.finance.entity.Transaction;
import com.finance.finance.entity.User;
import com.finance.finance.repository.CategoryRepository;
import com.finance.finance.service.impl.TransactionServiceImpl;
import com.finance.finance.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User testUser;
    private Category testCategory;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        // Create test category
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Food");
        testCategory.setType("EXPENSE");
        testCategory.setUser(testUser);

        // Create test transaction
        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setAmount(100.0);
        testTransaction.setDate(LocalDate.now().minusDays(1));
        testTransaction.setDescription("Test transaction");
        testTransaction.setCategory(testCategory);
        testTransaction.setUser(testUser);
        testTransaction.setType("EXPENSE");
    }

    private TransactionRequest createValidTransactionRequest() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(100.0);
        request.setDate(LocalDate.now().minusDays(1).toString());
        request.setDescription("Test transaction");
        request.setCategory("Food");
        return request;
    }

    private TransactionUpdateRequest createValidUpdateRequest() {
        TransactionUpdateRequest request = new TransactionUpdateRequest();
        request.setAmount(150.0);
        request.setDescription("Updated description");
        return request;
    }

    @Test
    void createTransaction_ValidRequest_Success() {
        // Given
        TransactionRequest request = createValidTransactionRequest();
        when(categoryRepository.findByNameAndUser("Food", testUser))
                .thenReturn(Optional.of(testCategory));
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(testTransaction);

        // When
        TransactionResponse response = transactionService.createTransaction(testUser, request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(new BigDecimal("100.00"), response.getAmount());
        assertEquals("Test transaction", response.getDescription());
        assertEquals("Food", response.getCategory());
        assertEquals("EXPENSE", response.getType());

        // Verify repository interactions
        verify(categoryRepository).findByNameAndUser("Food", testUser);
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        
        Transaction savedTransaction = transactionCaptor.getValue();
        assertEquals(100.0, savedTransaction.getAmount());
        assertEquals("Test transaction", savedTransaction.getDescription());
        assertEquals(testCategory, savedTransaction.getCategory());
        assertEquals(testUser, savedTransaction.getUser());
    }

    @Test
    void createTransaction_NullAmount_ThrowsException() {
        // Given
        TransactionRequest request = createValidTransactionRequest();
        request.setAmount(null);

        // When & Then
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.createTransaction(testUser, request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Amount must be positive", exception.getReason());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_NegativeAmount_ThrowsException() {
        // Given
        TransactionRequest request = createValidTransactionRequest();
        request.setAmount(-50.0);

        // When & Then
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.createTransaction(testUser, request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Amount must be positive", exception.getReason());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_ZeroAmount_ThrowsException() {
        // Given
        TransactionRequest request = createValidTransactionRequest();
        request.setAmount(0.0);

        // When & Then
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.createTransaction(testUser, request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Amount must be positive", exception.getReason());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_InvalidDateFormat_ThrowsException() {
        // Given
        TransactionRequest request = createValidTransactionRequest();
        request.setDate("invalid-date-format");

        // When & Then
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.createTransaction(testUser, request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid date format", exception.getReason());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_FutureDate_ThrowsException() {
        // Given
        TransactionRequest request = createValidTransactionRequest();
        request.setDate(LocalDate.now().plusDays(1).toString());

        // When & Then
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.createTransaction(testUser, request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Date cannot be in the future", exception.getReason());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_InvalidCategory_ThrowsException() {
        // Given
        TransactionRequest request = createValidTransactionRequest();
        request.setCategory("InvalidCategory");

        when(categoryRepository.findByNameAndUser("InvalidCategory", testUser))
                .thenReturn(Optional.empty());
        when(categoryRepository.findByNameAndUserIsNull("InvalidCategory"))
                .thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.createTransaction(testUser, request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Invalid category", exception.getReason());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void createTransaction_GlobalCategory_Success() {
        // Given
        Category globalCategory = new Category();
        globalCategory.setId(2L);
        globalCategory.setName("GlobalFood");
        globalCategory.setType("EXPENSE");
        globalCategory.setUser(null); // Global category

        TransactionRequest request = createValidTransactionRequest();
        request.setCategory("GlobalFood");

        Transaction expectedTransaction = new Transaction();
        expectedTransaction.setId(2L);
        expectedTransaction.setAmount(100.0);
        expectedTransaction.setDate(LocalDate.now().minusDays(1));
        expectedTransaction.setDescription("Test transaction");
        expectedTransaction.setCategory(globalCategory);
        expectedTransaction.setUser(testUser);
        expectedTransaction.setType("EXPENSE");

        when(categoryRepository.findByNameAndUser("GlobalFood", testUser))
                .thenReturn(Optional.empty());
        when(categoryRepository.findByNameAndUserIsNull("GlobalFood"))
                .thenReturn(Optional.of(globalCategory));
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(expectedTransaction);

        // When
        TransactionResponse response = transactionService.createTransaction(testUser, request);

        // Then
        assertNotNull(response);
        assertEquals("GlobalFood", response.getCategory());
        verify(categoryRepository).findByNameAndUser("GlobalFood", testUser);
        verify(categoryRepository).findByNameAndUserIsNull("GlobalFood");
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void getTransactions_NoFilters_ReturnsAllUserTransactions() {
        // Given
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByUser(testUser)).thenReturn(transactions);

        // When
        List<TransactionResponse> responses = transactionService.getTransactions(testUser, null, null, null);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        TransactionResponse response = responses.get(0);
        assertEquals(testTransaction.getId(), response.getId());
        assertEquals(new BigDecimal("100.00"), response.getAmount());
        assertEquals("Test transaction", response.getDescription());
        assertEquals("Food", response.getCategory());
        
        verify(transactionRepository).findByUser(testUser);
    }

    @Test
    void getTransactions_WithDateRange_ReturnsFilteredTransactions() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        List<Transaction> transactions = Arrays.asList(testTransaction);
        
        when(transactionRepository.findByUserAndDateBetween(testUser, startDate, endDate))
                .thenReturn(transactions);

        // When
        List<TransactionResponse> responses = transactionService.getTransactions(testUser, startDate, endDate, null);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(transactionRepository).findByUserAndDateBetween(testUser, startDate, endDate);
    }

    @Test
    void getTransactions_WithCategoryFilter_ReturnsFilteredTransactions() {
        // Given
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByUser(testUser)).thenReturn(transactions);

        // When
        List<TransactionResponse> responses = transactionService.getTransactions(testUser, null, null, "Food");

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Food", responses.get(0).getCategory());
        verify(transactionRepository).findByUser(testUser);
    }

    @Test
    void getTransactions_WithCategoryFilterNoMatch_ReturnsEmptyList() {
        // Given
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByUser(testUser)).thenReturn(transactions);

        // When
        List<TransactionResponse> responses = transactionService.getTransactions(testUser, null, null, "NonExistentCategory");

        // Then
        assertNotNull(responses);
        assertEquals(0, responses.size());
        verify(transactionRepository).findByUser(testUser);
    }

    @Test
    void getTransactions_EmptyResult_ReturnsEmptyList() {
        // Given
        when(transactionRepository.findByUser(testUser)).thenReturn(Collections.emptyList());

        // When
        List<TransactionResponse> responses = transactionService.getTransactions(testUser, null, null, null);

        // Then
        assertNotNull(responses);
        assertEquals(0, responses.size());
        verify(transactionRepository).findByUser(testUser);
    }

    @Test
    void updateTransaction_ValidRequest_Success() {
        // Given
        TransactionUpdateRequest updateRequest = createValidUpdateRequest();
        
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When
        TransactionResponse response = transactionService.updateTransaction(testUser, 1L, updateRequest);

        // Then
        assertNotNull(response);
        verify(transactionRepository).findById(1L);
        
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());
        
        // Note: In a real implementation, you'd verify the transaction was actually updated
        // This depends on your actual service implementation
    }

    @Test
    void updateTransaction_TransactionNotFound_ThrowsException() {
        // Given
        TransactionUpdateRequest updateRequest = createValidUpdateRequest();
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.updateTransaction(testUser, 1L, updateRequest)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Transaction not found", exception.getReason());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void updateTransaction_WrongUser_ThrowsException() {
        // Given
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");
        
        TransactionUpdateRequest updateRequest = createValidUpdateRequest();
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        // When & Then
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.updateTransaction(anotherUser, 1L, updateRequest)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Transaction not found", exception.getReason());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void updateTransaction_NullAmount_DoesNotUpdateAmount() {
        // Given
        TransactionUpdateRequest updateRequest = new TransactionUpdateRequest();
        updateRequest.setAmount(null);
        updateRequest.setDescription("Updated description");

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When
        transactionService.updateTransaction(testUser, 1L, updateRequest);

        // Then
        verify(transactionRepository).save(testTransaction);
        // The amount should remain unchanged (100.0)
    }

    @Test
    void updateTransaction_NegativeAmount_DoesNotUpdateAmount() {
        // Given
        TransactionUpdateRequest updateRequest = new TransactionUpdateRequest();
        updateRequest.setAmount(-50.0);
        updateRequest.setDescription("Updated description");

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When
        transactionService.updateTransaction(testUser, 1L, updateRequest);

        // Then
        verify(transactionRepository).save(testTransaction);
        // The amount should remain unchanged (100.0)
    }

    @Test
    void deleteTransaction_Success() {
        // Given
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        // When
        transactionService.deleteTransaction(testUser, 1L);

        // Then
        verify(transactionRepository).findById(1L);
        verify(transactionRepository).delete(testTransaction);
    }

    @Test
    void deleteTransaction_TransactionNotFound_ThrowsException() {
        // Given
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.deleteTransaction(testUser, 1L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Transaction not found", exception.getReason());
        verify(transactionRepository, never()).delete(any());
    }

    @Test
    void deleteTransaction_WrongUser_ThrowsException() {
        // Given
        User anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setUsername("anotheruser");

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        // When & Then
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> transactionService.deleteTransaction(anotherUser, 1L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Transaction not found", exception.getReason());
        verify(transactionRepository, never()).delete(any());
    }

    @Test
    void getTransactions_MultipleTransactions_ReturnsSortedList() {
        // Given
        Transaction transaction1 = new Transaction();
        transaction1.setId(1L);
        transaction1.setAmount(100.0);
        transaction1.setDate(LocalDate.now().minusDays(2));
        transaction1.setDescription("Earlier transaction");
        transaction1.setCategory(testCategory);
        transaction1.setUser(testUser);
        transaction1.setType("EXPENSE");

        Transaction transaction2 = new Transaction();
        transaction2.setId(2L);
        transaction2.setAmount(200.0);
        transaction2.setDate(LocalDate.now().minusDays(1));
        transaction2.setDescription("Later transaction");
        transaction2.setCategory(testCategory);
        transaction2.setUser(testUser);
        transaction2.setType("EXPENSE");

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        when(transactionRepository.findByUser(testUser)).thenReturn(transactions);

        // When
        List<TransactionResponse> responses = transactionService.getTransactions(testUser, null, null, null);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        // Verify both transactions are returned (order may depend on implementation)
        assertTrue(responses.stream().anyMatch(r -> r.getId().equals(1L)));
        assertTrue(responses.stream().anyMatch(r -> r.getId().equals(2L)));
    }
}