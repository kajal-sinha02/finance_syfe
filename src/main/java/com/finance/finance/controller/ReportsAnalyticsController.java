package com.finance.finance.controller;

import com.finance.finance.dto.response.MonthlyReportResponse;
import com.finance.finance.dto.response.YearlyReportResponse;
import com.finance.finance.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportsAnalyticsController {

    private final ReportService reportService;

    @GetMapping("/monthly")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
            Principal principal,
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(reportService.getMonthlyReport(principal.getName(), month, year));
    }

    @GetMapping("/yearly")
    public ResponseEntity<YearlyReportResponse> getYearlyReport(
            Principal principal,
            @RequestParam int year) {
        return ResponseEntity.ok(reportService.getYearlyReport(principal.getName(), year));
    }
}
