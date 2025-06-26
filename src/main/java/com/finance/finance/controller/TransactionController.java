package com.finance.finance.controller;

import com.finance.finance.dto.request.TransactionRequest;
import com.finance.finance.dto.request.TransactionUpdateRequest;
import com.finance.finance.dto.response.TransactionResponse;
import com.finance.finance.entity.User;
import com.finance.finance.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// controllers for transaction endpoints
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // create transaction endpoint
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@SessionAttribute("user") User user,
                                                                 @RequestBody TransactionRequest request) {
        TransactionResponse created = transactionService.createTransaction(user, request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // get transaction endpoint
    
    @GetMapping
public ResponseEntity<Map<String, List<TransactionResponse>>> getTransactions(
        @SessionAttribute("user") User user,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(required = false) String category // changed from Long categoryId
) {
    List<TransactionResponse> list = transactionService.getTransactions(user, startDate, endDate, category);
    Map<String, List<TransactionResponse>> response = new HashMap<>();
    response.put("transactions", list);
    return ResponseEntity.ok(response);
}

// update transaction endpoint

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(@SessionAttribute("user") User user,
                                                                 @PathVariable Long id,
                                                                 @RequestBody TransactionUpdateRequest request) {
        TransactionResponse updated = transactionService.updateTransaction(user, id, request);
        return ResponseEntity.ok(updated);
    }

    // delete transaction endpoint

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTransaction(@SessionAttribute("user") User user,
                                                                 @PathVariable Long id) {
        transactionService.deleteTransaction(user, id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Transaction deleted successfully");
        return ResponseEntity.ok(response);
    }
}
