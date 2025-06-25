package com.finance.finance.service;

import com.finance.finance.dto.response.MonthlyReportResponse;
import com.finance.finance.dto.response.YearlyReportResponse;

public interface ReportService {
    MonthlyReportResponse getMonthlyReport(Long userId, int month, int year);
    YearlyReportResponse getYearlyReport(Long userId, int year);
}