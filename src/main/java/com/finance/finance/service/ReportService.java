package com.finance.finance.service;

import com.finance.finance.dto.response.ReportResponse;
import com.finance.finance.entity.User;

//interface for report services
public interface ReportService {
    // get monthly response
    ReportResponse getMonthlyReport(User user, int year, int month);
    // get yearly response
    ReportResponse getYearlyReport(User user, int year);
}