package com.finance.finance.service;

import com.finance.finance.dto.response.MonthlyReportResponse;
import com.finance.finance.dto.response.YearlyReportResponse;

public interface ReportService {
    MonthlyReportResponse getMonthlyReport(String username, int month, int year);
    YearlyReportResponse getYearlyReport(String username, int year);
}