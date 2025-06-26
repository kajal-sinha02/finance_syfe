package com.finance.finance.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

@Data
@Builder
// saving goal response dto
public class SavingsGoalResponse {
    private Long id;
    private String goalName;
    private BigDecimal targetAmount;
    private String targetDate;
    private String startDate;
    private BigDecimal currentProgress;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "0.0")
    private BigDecimal progressPercentage;

    private BigDecimal remainingAmount;
}
