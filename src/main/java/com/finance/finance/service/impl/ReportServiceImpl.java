package com.finance.finance.service.impl;

import com.finance.finance.dto.response.ReportResponse;
import com.finance.finance.entity.User;
import com.finance.finance.repository.TransactionRepository;
import com.finance.finance.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;

    public ReportServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public ReportResponse getMonthlyReport(User user, int year, int month) {
        try {
            if (month < 1 || month > 12) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid month: " + month);
            }
            LocalDate start = YearMonth.of(year, month).atDay(1);
            LocalDate end = YearMonth.of(year, month).atEndOfMonth();

            return buildReport(user, start, end, year, month);
        } catch (DateTimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid month/year input", e);
        }
    }

    @Override
    public ReportResponse getYearlyReport(User user, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        return buildReport(user, start, end, year, null);
    }

    private ReportResponse buildReport(User user, LocalDate start, LocalDate end, int year, Integer month) {
        Map<String, BigDecimal> income = getCategorySums(user, start, end, "INCOME");
        Map<String, BigDecimal> expense = getCategorySums(user, start, end, "EXPENSE");

        BigDecimal totalIncome = sum(income);
        BigDecimal totalExpense = sum(expense);
       BigDecimal netSavings = totalIncome.subtract(totalExpense);
if (netSavings.compareTo(BigDecimal.ZERO) == 0) {
    netSavings = BigDecimal.ZERO; // return as plain 0 to match validation
} else {
    netSavings = netSavings.setScale(2, RoundingMode.HALF_UP);
}

        // DEBUG LOGS
        System.out.println("=== Report Debug Info ===");
        System.out.println("User ID: " + user.getId());
        System.out.println("Period: " + start + " to " + end);
        System.out.println("Income Map: " + income);
        System.out.println("Expense Map: " + expense);
        System.out.println("Total Income: " + totalIncome);
        System.out.println("Total Expense: " + totalExpense);
        System.out.println("Net Savings: " + netSavings);
        System.out.println("=========================");

        return ReportResponse.builder()
                .year(year)
                .month(month != null ? month : 0)
                .totalIncome(income)       // Already scaled
                .totalExpenses(expense)    // Already scaled
                .netSavings(netSavings)
                .build();
    }

    private Map<String, BigDecimal> getCategorySums(User user, LocalDate start, LocalDate end, String type) {
    List<Object[]> result = transactionRepository.sumAmountByCategoryAndType(user, start, end, type);
    Map<String, BigDecimal> map = new HashMap<>();

    System.out.println("Querying " + type + " transactions from " + start + " to " + end + " for user " + user.getId());
    System.out.println("Result size: " + result.size());

    for (Object[] row : result) {
        String categoryName = (String) row[0];
        Object amountObj = row[1];

        BigDecimal amount = BigDecimal.ZERO;

        if (amountObj instanceof BigDecimal) {
            amount = ((BigDecimal) amountObj);
        } else if (amountObj instanceof Number) {
            amount = BigDecimal.valueOf(((Number) amountObj).doubleValue());
        }

        amount = amount.setScale(2, RoundingMode.HALF_UP);
        map.put(categoryName, amount);

        // Debug log
        System.out.println("Category: " + categoryName + ", Raw: " + amountObj + ", Parsed: " + amount);
    }

    return map;
}


    private BigDecimal sum(Map<String, BigDecimal> data) {
        if (data == null || data.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return data.values().stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), BigDecimal::add);
    }
}
