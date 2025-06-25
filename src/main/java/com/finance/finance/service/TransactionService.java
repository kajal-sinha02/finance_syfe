package com.finance.finance.service;

import com.finance.finance.dto.request.TransactionRequest;
import com.finance.finance.dto.response.TransactionResponse;
import com.finance.finance.entity.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    TransactionResponse createTransaction(TransactionRequest request, Long userId);
    TransactionResponse getTransactionById(Long id);
    
    List<TransactionResponse> getAllTransactions(
        Long userId,
        LocalDate startDate,
        LocalDate endDate,
        Long categoryId,
        String type
    );

    TransactionResponse updateTransaction(Long id, TransactionRequest request);
    void deleteTransaction(Long id);
    List<TransactionResponse> getTransactions(Long userId, String type, Long categoryId, LocalDate startDate, LocalDate endDate);
    Optional<BigDecimal> sumAmountByUserAndTypeAndDateAfter(User user, String type, LocalDate startDate);
}