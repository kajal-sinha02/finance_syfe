package com.finance.finance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class YearlyReportResponse {
    private int year;
    private Map<Integer, MonthlyReportResponse> monthlyReports;
}