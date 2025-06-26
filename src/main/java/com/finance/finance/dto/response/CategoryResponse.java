package com.finance.finance.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

// category response dto

public class CategoryResponse {
    private String name;
    private String type;

   @JsonProperty("isCustom") 
    private boolean isCustom;
    // ✅ Add this virtual field for testing/compatibility
    @JsonProperty("custom")
    public boolean getCustom() {
        return isCustom;
    }
    @JsonProperty("custom")
public boolean isCustom() {
    return isCustom;
}
}
