package com.finance.finance.service;

import com.finance.finance.dto.response.ReportResponse;
import com.finance.finance.entity.User;
import com.finance.finance.repository.TransactionRepository;
import com.finance.finance.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
    }

    @Test
    void getMonthlyReport_ValidMonth_ReturnsCorrectReport() {
        int year = 2025;
        int month = 6;
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Object[]> incomeData = Arrays.asList(new Object[][] {
            { "Salary", BigDecimal.valueOf(5000) }
        });
        List<Object[]> expenseData = Arrays.asList(new Object[][] {
            { "Rent", BigDecimal.valueOf(3000) }
        });

        when(transactionRepository.sumAmountByCategoryAndType(testUser, start, end, "INCOME"))
                .thenReturn(incomeData);
        when(transactionRepository.sumAmountByCategoryAndType(testUser, start, end, "EXPENSE"))
                .thenReturn(expenseData);

        ReportResponse response = reportService.getMonthlyReport(testUser, year, month);

        assertEquals(year, response.getYear());
        assertEquals(month, response.getMonth());
        assertEquals(0, response.getNetSavings().compareTo(BigDecimal.valueOf(2000.00)));
        assertEquals(0, response.getTotalIncome().get("Salary").compareTo(BigDecimal.valueOf(5000.00)));
        assertEquals(0, response.getTotalExpenses().get("Rent").compareTo(BigDecimal.valueOf(3000.00)));
    }

    @Test
    void getMonthlyReport_InvalidMonth_ThrowsException() {
        int year = 2025;
        int month = 13;

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> reportService.getMonthlyReport(testUser, year, month));

        assertEquals("Invalid month: 13", ex.getReason());
    }

    @Test
    void getYearlyReport_ValidYear_ReturnsCorrectReport() {
        int year = 2025;
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<Object[]> incomeData = Arrays.asList(new Object[][] {
            { "Freelance", BigDecimal.valueOf(12000) }
        });
        List<Object[]> expenseData = Arrays.asList(new Object[][] {
            { "Travel", BigDecimal.valueOf(5000) }
        });

        when(transactionRepository.sumAmountByCategoryAndType(testUser, start, end, "INCOME"))
                .thenReturn(incomeData);
        when(transactionRepository.sumAmountByCategoryAndType(testUser, start, end, "EXPENSE"))
                .thenReturn(expenseData);

        ReportResponse response = reportService.getYearlyReport(testUser, year);

        assertEquals(year, response.getYear());
        assertEquals(0, response.getMonth());
        assertEquals(0, response.getNetSavings().compareTo(BigDecimal.valueOf(7000.00)));
        assertEquals(0, response.getTotalIncome().get("Freelance").compareTo(BigDecimal.valueOf(12000.00)));
        assertEquals(0, response.getTotalExpenses().get("Travel").compareTo(BigDecimal.valueOf(5000.00)));
    }

    @Test
    void getMonthlyReport_ZeroSavings_ReturnsZeroNet() {
        int year = 2025;
        int month = 6;
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Object[]> incomeData = Arrays.asList(new Object[][] {
            { "Work", BigDecimal.valueOf(4000) }
        });
        List<Object[]> expenseData = Arrays.asList(new Object[][] {
            { "Bills", BigDecimal.valueOf(4000) }
        });

        when(transactionRepository.sumAmountByCategoryAndType(testUser, start, end, "INCOME"))
                .thenReturn(incomeData);
        when(transactionRepository.sumAmountByCategoryAndType(testUser, start, end, "EXPENSE"))
                .thenReturn(expenseData);

        ReportResponse response = reportService.getMonthlyReport(testUser, year, month);

        assertEquals(0, response.getNetSavings().compareTo(BigDecimal.valueOf(0.00)));
    }

    @Test
    void getYearlyReport_EmptyData_ReturnsZeroes() {
        int year = 2025;
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        when(transactionRepository.sumAmountByCategoryAndType(testUser, start, end, "INCOME"))
                .thenReturn(Collections.emptyList());
        when(transactionRepository.sumAmountByCategoryAndType(testUser, start, end, "EXPENSE"))
                .thenReturn(Collections.emptyList());

        ReportResponse response = reportService.getYearlyReport(testUser, year);

        assertEquals(0, response.getNetSavings().compareTo(BigDecimal.valueOf(0.00)));
        assertTrue(response.getTotalIncome().isEmpty());
        assertTrue(response.getTotalExpenses().isEmpty());
    }
}
