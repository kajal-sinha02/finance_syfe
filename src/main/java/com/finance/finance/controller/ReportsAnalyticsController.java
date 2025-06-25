package com.finance.finance.controller;

import com.finance.finance.dto.response.MonthlyReportResponse;
import com.finance.finance.dto.response.YearlyReportResponse;
import com.finance.finance.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportsAnalyticsController {

    private final ReportService reportService;

    @Autowired
    public ReportsAnalyticsController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
            @RequestParam Long userId,
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(reportService.getMonthlyReport(userId, month, year));
    }

    @GetMapping("/yearly")
    public ResponseEntity<YearlyReportResponse> getYearlyReport(
            @RequestParam Long userId,
            @RequestParam int year) {
        return ResponseEntity.ok(reportService.getYearlyReport(userId, year));
    }
}