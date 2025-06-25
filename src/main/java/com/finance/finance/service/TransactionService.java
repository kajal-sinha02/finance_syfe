package com.finance.finance.service;

import com.finance.finance.dto.request.TransactionRequest;
import com.finance.finance.dto.response.TransactionResponse;
import com.finance.finance.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionService {

    TransactionResponse createTransaction(TransactionRequest request, User user);

    TransactionResponse getTransactionById(Long id, User user);

    List<TransactionResponse> getTransactions(User user, String type, Long categoryId, LocalDate startDate, LocalDate endDate);

    TransactionResponse updateTransaction(Long id, TransactionRequest request, User user);

    void deleteTransaction(Long id, User user);

    Optional<BigDecimal> sumAmountByUserAndTypeAndDateAfter(User user, String type, LocalDate startDate);
}
