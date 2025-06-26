package com.finance.finance.dto.response;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

@Data
@Builder
// report response dto 
public class ReportResponse {
    private int month; // 0 for yearly
    private int year;

    @Builder.Default
    private Map<String, BigDecimal> totalIncome = Collections.emptyMap();

    @Builder.Default
    private Map<String, BigDecimal> totalExpenses = Collections.emptyMap();

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Builder.Default
    private BigDecimal netSavings = BigDecimal.ZERO.setScale(2);
}
