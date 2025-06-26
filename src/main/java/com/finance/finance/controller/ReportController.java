package com.finance.finance.controller;

import com.finance.finance.dto.response.ReportResponse;
import com.finance.finance.entity.User;
import com.finance.finance.service.ReportService;
import org.springframework.web.bind.annotation.*;

// controllers for getting monthly and yearly endpoints
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // monthly report endpoint
    @GetMapping("/monthly/{year}/{month}")
    public ReportResponse getMonthly(@SessionAttribute("user") User user,
            @PathVariable int year,
            @PathVariable int month) {
        return reportService.getMonthlyReport(user, year, month);
    }

    // yearly report endpoint

    @GetMapping("/yearly/{year}")
    public ReportResponse getYearly(@SessionAttribute("user") User user,
            @PathVariable int year) {
        return reportService.getYearlyReport(user, year);
    }
}
