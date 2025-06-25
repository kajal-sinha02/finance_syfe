package com.finance.finance.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {
    private String name;
    private String type; // "INCOME" or "EXPENSE"
}
