package com.finance.finance.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionUpdateRequest {

    @NotNull
    @Positive
    private Double amount;

    private String description;

    @NotNull
    private Long categoryId;
}
