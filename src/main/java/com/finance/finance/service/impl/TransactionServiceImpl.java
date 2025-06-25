package com.finance.finance.service.impl;

import com.finance.finance.dto.request.TransactionRequest;
import com.finance.finance.dto.response.TransactionResponse;
import com.finance.finance.entity.Category;
import com.finance.finance.entity.Transaction;
import com.finance.finance.entity.User;
import com.finance.finance.exception.ResourceNotFoundException;
import com.finance.finance.repository.CategoryRepository;
import com.finance.finance.repository.TransactionRepository;
import com.finance.finance.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                  UserRepository userRepository,
                                  CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public TransactionResponse createTransaction(TransactionRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        Transaction transaction = new Transaction();
        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(request.getAmount());
        transaction.setDate(request.getDate());
        // Normalize transaction type to uppercase and trim
        transaction.setType(request.getType() != null ? request.getType().trim().toUpperCase() : null);
        transaction.setDescription(request.getDescription());

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    @Override
    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));
        return mapToResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getAllTransactions(Long userId, LocalDate startDate, LocalDate endDate, Long categoryId, String type) {
        List<Transaction> transactions = transactionRepository.findFiltered(userId, startDate, endDate, categoryId, type);
        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with ID: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        transaction.setAmount(request.getAmount());
        transaction.setDate(request.getDate());
        // Normalize type on update as well
        transaction.setType(request.getType() != null ? request.getType().trim().toUpperCase() : null);
        transaction.setDescription(request.getDescription());
        transaction.setCategory(category);

        Transaction updated = transactionRepository.save(transaction);
        return mapToResponse(updated);
    }

    @Override
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Transaction not found with ID: " + id);
        }
        transactionRepository.deleteById(id);
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
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

    @Override
    public List<TransactionResponse> getTransactions(Long userId, String type, Long categoryId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findFiltered(userId, startDate, endDate, categoryId, type)
                .stream()
                .map(txn -> {
                    TransactionResponse res = new TransactionResponse();
                    res.setId(txn.getId());
                    res.setAmount(txn.getAmount());
                    res.setDate(txn.getDate());
                    res.setType(txn.getType());
                    res.setDescription(txn.getDescription());
                    res.setCategoryId(txn.getCategory().getId());
                    res.setCategoryName(txn.getCategory().getName());
                    return res;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<BigDecimal> sumAmountByUserAndTypeAndDateAfter(User user, String type, LocalDate startDate) {
        return transactionRepository.getSumAmountByUserAndTypeAndDateAfter(user, type, startDate);
    }
}
