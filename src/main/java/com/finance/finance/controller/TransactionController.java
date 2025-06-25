// TransactionController.java
package com.finance.finance.controller;

import com.finance.finance.dto.request.TransactionRequest;
import com.finance.finance.dto.response.TransactionResponse;
import com.finance.finance.entity.User;
import com.finance.finance.service.TransactionService;
import com.finance.finance.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    private User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request,
            HttpSession session
    ) {
        User user = getCurrentUser(session);
        TransactionResponse created = transactionService.createTransaction(request, user);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable Long id,
            HttpSession session
    ) {
        User user = getCurrentUser(session);
        TransactionResponse transaction = transactionService.getTransactionById(id, user);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpSession session
    ) {
        User user = getCurrentUser(session);
        List<TransactionResponse> transactions = transactionService.getTransactions(user, type, categoryId, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request,
            HttpSession session
    ) {
        User user = getCurrentUser(session);
        TransactionResponse updated = transactionService.updateTransaction(id, request, user);
        return ResponseEntity.ok(updated);
    }

  @DeleteMapping("/{id}")
public ResponseEntity<Map<String, String>> deleteTransaction(
        @PathVariable Long id,
        HttpSession session
) {
    User user = getCurrentUser(session);
    transactionService.deleteTransaction(id, user);

    Map<String, String> response = new HashMap<>();
    response.put("message", "Transaction deleted successfully");

    return ResponseEntity.ok(response); // returns 200 OK with body
}
}
