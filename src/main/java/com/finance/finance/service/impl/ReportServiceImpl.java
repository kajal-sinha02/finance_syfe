package com.finance.finance.service.impl;

import com.finance.finance.dto.response.MonthlyReportResponse;
import com.finance.finance.dto.response.YearlyReportResponse;
import com.finance.finance.entity.Transaction;
import com.finance.finance.entity.User;
import com.finance.finance.repository.TransactionRepository;
import com.finance.finance.repository.UserRepository;
import com.finance.finance.service.ReportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    public MonthlyReportResponse getMonthlyReport(String username, int month, int year) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(user.getId(), start, end);

        Map<String, BigDecimal> incomeByCategory = transactions.stream()
                .filter(tx -> tx.getType() != null && tx.getCategory() != null)
                .filter(tx -> "INCOME".equalsIgnoreCase(tx.getType().trim()))
                .collect(Collectors.groupingBy(
                        tx -> tx.getCategory().getName(),
                        Collectors.mapping(
                                tx -> BigDecimal.valueOf(tx.getAmount()),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        Map<String, BigDecimal> expenseByCategory = transactions.stream()
                .filter(tx -> tx.getType() != null && tx.getCategory() != null)
                .filter(tx -> "EXPENSE".equalsIgnoreCase(tx.getType().trim()))
                .collect(Collectors.groupingBy(
                        tx -> tx.getCategory().getName(),
                        Collectors.mapping(
                                tx -> BigDecimal.valueOf(tx.getAmount()),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        BigDecimal totalIncome = incomeByCategory.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpense = expenseByCategory.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal netSavings = totalIncome.subtract(totalExpense);

        return new MonthlyReportResponse(month, year, incomeByCategory, expenseByCategory, netSavings);
    }

    @Override
    public YearlyReportResponse getYearlyReport(String username, int year) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + username));

        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(user.getId(), start, end);

        Map<String, BigDecimal> incomeByCategory = transactions.stream()
                .filter(tx -> tx.getType() != null && tx.getCategory() != null)
                .filter(tx -> "INCOME".equalsIgnoreCase(tx.getType().trim()))
                .collect(Collectors.groupingBy(
                        tx -> tx.getCategory().getName(),
                        Collectors.mapping(
                                tx -> BigDecimal.valueOf(tx.getAmount()),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        Map<String, BigDecimal> expenseByCategory = transactions.stream()
                .filter(tx -> tx.getType() != null && tx.getCategory() != null)
                .filter(tx -> "EXPENSE".equalsIgnoreCase(tx.getType().trim()))
                .collect(Collectors.groupingBy(
                        tx -> tx.getCategory().getName(),
                        Collectors.mapping(
                                tx -> BigDecimal.valueOf(tx.getAmount()),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));

        BigDecimal totalIncome = incomeByCategory.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpense = expenseByCategory.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal netSavings = totalIncome.subtract(totalExpense);

        return new YearlyReportResponse(year, incomeByCategory, expenseByCategory, netSavings);
    }
}
