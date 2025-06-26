package com.finance.finance.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data

// saving goal update request dto
public class SavingsGoalUpdateRequest {
    private String goalName;
    private BigDecimal targetAmount;
    private LocalDate targetDate;
}
