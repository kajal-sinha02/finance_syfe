package com.finance.finance.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionUpdateRequest {
    private Double amount;
    private String description;
}
