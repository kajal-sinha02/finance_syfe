package com.finance.finance.service;

import com.finance.finance.dto.request.TransactionRequest;
import com.finance.finance.dto.request.TransactionUpdateRequest;
import com.finance.finance.dto.response.TransactionResponse;
import com.finance.finance.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    TransactionResponse createTransaction(User user, TransactionRequest request);
List<TransactionResponse> getTransactions(User user, LocalDate start, LocalDate end, String categoryName);
    TransactionResponse updateTransaction(User user, Long id, TransactionUpdateRequest request);
    void deleteTransaction(User user, Long id);
}
