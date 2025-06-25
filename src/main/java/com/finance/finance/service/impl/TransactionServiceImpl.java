package com.finance.finance.service.impl;

import com.finance.finance.dto.request.TransactionRequest;
import com.finance.finance.dto.response.TransactionResponse;
import com.finance.finance.entity.Category;
import com.finance.finance.entity.Transaction;
import com.finance.finance.entity.User;
import com.finance.finance.exception.ResourceNotFoundException;
import com.finance.finance.repository.CategoryRepository;
import com.finance.finance.repository.TransactionRepository;
import com.finance.finance.service.TransactionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public TransactionResponse createTransaction(TransactionRequest request, User user) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .filter(c -> c.getUser() == null || c.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Category not found or not owned by user"));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(request.getAmount());
        transaction.setDate(request.getDate());
        transaction.setType(request.getType() != null ? request.getType().trim().toUpperCase() : null);
        transaction.setDescription(request.getDescription());

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    @Override
    public TransactionResponse getTransactionById(Long id, User user) {
        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or you do not have access"));
        return mapToResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getTransactions(User user, String type, Long categoryId, LocalDate startDate, LocalDate endDate) {
        // ✅ Normalize type to uppercase before passing to query
        String normalizedType = (type != null) ? type.trim().toUpperCase() : null;

        return transactionRepository.findFiltered(user.getId(), startDate, endDate, categoryId, normalizedType)
                .stream()
                .filter(tx -> tx.getCategory() != null)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
public TransactionResponse updateTransaction(Long id, TransactionRequest request, User user) {
    Transaction transaction = transactionRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or not accessible"));

    Category category = categoryRepository.findById(request.getCategoryId())
            .filter(c -> c.getUser() == null || c.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new ResourceNotFoundException("Category not found or not accessible"));

    // Only update fields except date
    transaction.setAmount(request.getAmount());
    transaction.setType(request.getType() != null ? request.getType().trim().toUpperCase() : null);
    transaction.setDescription(request.getDescription());
    transaction.setCategory(category);

    Transaction updated = transactionRepository.save(transaction);
    return mapToResponse(updated);
}

    // @Override
    // public TransactionResponse updateTransaction(Long id, TransactionRequest request, User user) {
    //     Transaction transaction = transactionRepository.findByIdAndUser(id, user)
    //             .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or not accessible"));

    //     Category category = categoryRepository.findById(request.getCategoryId())
    //             .filter(c -> c.getUser() == null || c.getUser().getId().equals(user.getId()))
    //             .orElseThrow(() -> new ResourceNotFoundException("Category not found or not accessible"));

    //     transaction.setAmount(request.getAmount());
    //     transaction.setDate(request.getDate());
    //     transaction.setType(request.getType() != null ? request.getType().trim().toUpperCase() : null);
    //     transaction.setDescription(request.getDescription());
    //     transaction.setCategory(category);

    //     Transaction updated = transactionRepository.save(transaction);
    //     return mapToResponse(updated);
    // }

    @Override
    public void deleteTransaction(Long id, User user) {
        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or you do not have access"));

        transactionRepository.delete(transaction);
    }

    @Override
    public Optional<BigDecimal> sumAmountByUserAndTypeAndDateAfter(User user, String type, LocalDate startDate) {
        String normalizedType = (type != null) ? type.trim().toUpperCase() : null;
        return transactionRepository.getSumAmountByUserAndTypeAndDateAfter(user, normalizedType, startDate);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        if (transaction.getCategory() == null) {
            throw new ResourceNotFoundException("Transaction has no associated category");
        }

        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setDate(transaction.getDate());
        response.setType(transaction.getType());
        response.setDescription(transaction.getDescription());
        response.setCategoryId(transaction.getCategory().getId());
        response.setCategoryName(transaction.getCategory().getName());
        return response;
    }
}
