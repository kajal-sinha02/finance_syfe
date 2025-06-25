package com.finance.finance.service.impl;

import com.finance.finance.dto.response.MonthlyReportResponse;
import com.finance.finance.dto.response.YearlyReportResponse;
import com.finance.finance.entity.Transaction;
import com.finance.finance.entity.User;
import com.finance.finance.repository.TransactionRepository;
import com.finance.finance.repository.UserRepository;
import com.finance.finance.service.ReportService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReportServiceImpl(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public MonthlyReportResponse getMonthlyReport(Long userId, int month, int year) {
        // Validate user
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Date range
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        // Fetch transactions
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(userId, start, end);

        // Aggregate income and expenses using streams
        Map<String, BigDecimal> incomeByCategory = transactions.stream()
            .filter(tx -> tx.getType() != null && tx.getCategory() != null)
            .filter(tx -> "INCOME".equalsIgnoreCase(tx.getType().trim()))
            .collect(Collectors.groupingBy(
                tx -> tx.getCategory().getName(),
                Collectors.mapping(tx -> BigDecimal.valueOf(tx.getAmount()),
                                   Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
            ));

        Map<String, BigDecimal> expenseByCategory = transactions.stream()
            .filter(tx -> tx.getType() != null && tx.getCategory() != null)
            .filter(tx -> "EXPENSE".equalsIgnoreCase(tx.getType().trim()))
            .collect(Collectors.groupingBy(
                tx -> tx.getCategory().getName(),
                Collectors.mapping(tx -> BigDecimal.valueOf(tx.getAmount()),
                                   Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
            ));

        // Totals
        BigDecimal totalIncome = incomeByCategory.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpense = expenseByCategory.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Net savings
        BigDecimal netSavings = totalIncome.subtract(totalExpense);

        return new MonthlyReportResponse(incomeByCategory, expenseByCategory, netSavings);
    }

    @Override
    public YearlyReportResponse getYearlyReport(Long userId, int year) {
        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Map<Integer, MonthlyReportResponse> monthlyReports = new HashMap<>();
        for (int m = 1; m <= 12; m++) {
            monthlyReports.put(m, getMonthlyReport(userId, m, year));
        }
        return new YearlyReportResponse(year, monthlyReports);
    }
}
