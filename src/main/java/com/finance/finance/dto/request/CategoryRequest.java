package com.finance.finance.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

// category request dto
public class CategoryRequest {
    private String name;
    private String type;
}
