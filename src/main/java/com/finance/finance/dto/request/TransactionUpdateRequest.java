package com.finance.finance.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

// transaction update request dto
public class TransactionUpdateRequest {
    private Double amount;
     private String date;       
    private String category; 
    private String description;
}
