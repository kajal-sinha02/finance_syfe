package com.finance.finance.dto.request;

import java.time.LocalDate;

public class TransactionRequest {
    private Double amount;
    private LocalDate date;
    private String type; // e.g., "income" or "expense"
    private String description;
    private Long categoryId;

    // Getters and Setters
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
}
