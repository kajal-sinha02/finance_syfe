package com.finance.finance.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

// transaction request dto
public class TransactionRequest {
    private Double amount;
    private String date;
    private String category; // name
    private String description;
}
