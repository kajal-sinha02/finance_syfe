package com.finance.finance.service;

import com.finance.finance.dto.request.TransactionRequest;
import com.finance.finance.dto.request.TransactionUpdateRequest;
import com.finance.finance.dto.response.TransactionResponse;
import com.finance.finance.entity.User;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing transaction services.
 */

public interface TransactionService {
    // create
    TransactionResponse createTransaction(User user, TransactionRequest request);

    // get
    List<TransactionResponse> getTransactions(User user, LocalDate start, LocalDate end, String categoryName);

    // update
    TransactionResponse updateTransaction(User user, Long id, TransactionUpdateRequest request);

    // delete
    void deleteTransaction(User user, Long id);
}
