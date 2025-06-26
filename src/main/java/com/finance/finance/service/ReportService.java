package com.finance.finance.service;

import com.finance.finance.dto.response.ReportResponse;
import com.finance.finance.entity.User;

public interface ReportService {
    ReportResponse getMonthlyReport(User user, int year, int month);
    ReportResponse getYearlyReport(User user, int year);
}