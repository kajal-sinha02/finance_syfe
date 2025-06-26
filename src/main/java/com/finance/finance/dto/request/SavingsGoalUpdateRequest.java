package com.finance.finance.dto.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SavingsGoalUpdateRequest {
    private BigDecimal targetAmount;
    private LocalDate targetDate;
}
