package com.finance.finance.controller;

import com.finance.finance.dto.response.ReportResponse;
import com.finance.finance.entity.User;
import com.finance.finance.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReportControllerTest {

    private MockMvc mockMvc;
    private ReportService reportService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private User mockUser;

    @BeforeEach
    void setUp() {
        // Configure ObjectMapper for potential date serialization
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        reportService = mock(ReportService.class);
        ReportController controller = new ReportController(reportService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // Setup mock user for session management
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("test@example.com");
        mockUser.setFullName("Test User");
    }

    @Test
    void testGetMonthlyReport() throws Exception {
        ReportResponse response = ReportResponse.builder()
                .month(6)
                .year(2025)
                .totalIncome(Map.of("salary", BigDecimal.valueOf(5000)))
                .totalExpenses(Map.of("rent", BigDecimal.valueOf(3000)))
                .netSavings(BigDecimal.valueOf(2000).setScale(2))
                .build();

        when(reportService.getMonthlyReport(eq(mockUser), eq(2025), eq(6)))
                .thenReturn(response);

        mockMvc.perform(get("/api/reports/monthly/2025/6")
                        .sessionAttr("user", mockUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value(6))
                .andExpect(jsonPath("$.year").value(2025))
                .andExpect(jsonPath("$.totalIncome.salary").value(5000))
                .andExpect(jsonPath("$.totalExpenses.rent").value(3000))
                .andExpect(jsonPath("$.netSavings").value("2000.00"));
    }

    @Test
    void testGetYearlyReport() throws Exception {
        ReportResponse response = ReportResponse.builder()
                .month(0)
                .year(2025)
                .totalIncome(Map.of("salary", BigDecimal.valueOf(60000)))
                .totalExpenses(Map.of("rent", BigDecimal.valueOf(40000)))
                .netSavings(BigDecimal.valueOf(20000).setScale(2))
                .build();

        when(reportService.getYearlyReport(eq(mockUser), eq(2025)))
                .thenReturn(response);

        mockMvc.perform(get("/api/reports/yearly/2025")
                        .sessionAttr("user", mockUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.month").value(0))
                .andExpect(jsonPath("$.year").value(2025))
                .andExpect(jsonPath("$.totalIncome.salary").value(60000))
                .andExpect(jsonPath("$.totalExpenses.rent").value(40000))
                .andExpect(jsonPath("$.netSavings").value("20000.00"));
    }
}