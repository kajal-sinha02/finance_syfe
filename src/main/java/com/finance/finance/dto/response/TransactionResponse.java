package com.finance.finance.dto.response;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    private Long id;
   private BigDecimal amount;
    private String date;
    private String category;
    private String description;
    private String type;
}
