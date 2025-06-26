package com.finance.finance.service.impl;

import com.finance.finance.dto.request.TransactionRequest;
import com.finance.finance.dto.request.TransactionUpdateRequest;
import com.finance.finance.dto.response.TransactionResponse;
import com.finance.finance.entity.Category;
import com.finance.finance.entity.Transaction;
import com.finance.finance.entity.User;
import com.finance.finance.repository.CategoryRepository;
import com.finance.finance.repository.TransactionRepository;
import com.finance.finance.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

// transaction service
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    // create transaction
    @Override
    public TransactionResponse createTransaction(User user, TransactionRequest request) {
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive");
        }

        LocalDate date;
        try {
            date = LocalDate.parse(request.getDate());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date format");
        }

        if (date.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date cannot be in the future");
        }

        Category category = findCategoryByName(user, request.getCategory());

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .date(date)
                .description(request.getDescription())
                .category(category)
                .user(user)
                .type(category.getType().toUpperCase())
                .build();

        transactionRepository.save(transaction);
        return mapToResponse(transaction);
    }

    // get transaction
@Override
public List<TransactionResponse> getTransactions(User user, LocalDate start, LocalDate end, String categoryName) {
    List<Transaction> transactions;

    if (start != null && end != null) {
        transactions = transactionRepository.findByUserAndDateBetween(user, start, end);
    } else {
        transactions = transactionRepository.findByUser(user);
    }

    return transactions.stream()
            .filter(t -> categoryName == null || t.getCategory().getName().equalsIgnoreCase(categoryName))
            .map(this::mapToResponse)
            .collect(Collectors.toList());
}
// update transaction

    @Override
    public TransactionResponse updateTransaction(User user, Long id, TransactionUpdateRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        if (request.getAmount() != null && request.getAmount() > 0) {
            transaction.setAmount(request.getAmount());
        }

        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }

        transactionRepository.save(transaction);
        return mapToResponse(transaction);
    }

    // delete transaction 

    @Override
    public void deleteTransaction(User user, Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));

        transactionRepository.delete(transaction);
    }
    // map the responses

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(new BigDecimal(String.format("%.2f", transaction.getAmount())))
                .date(transaction.getDate().toString())
                .category(transaction.getCategory().getName())
                .description(transaction.getDescription())
                .type(transaction.getType()) // ✅ included in response
                .build();
    }

     private Category findCategoryByName(User user, String name) {
        Category category = categoryRepository.findByNameAndUser(name, user)
                .or(() -> categoryRepository.findByNameAndUserIsNull(name))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid category"));

        System.out.println("Resolved category: " + category.getName() + ", type: " + category.getType()); // DEBUG

        return category;
    }
}
