/**
 * Represents a transaction category (either income or expense).
 */
package com.finance.finance.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "categories",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "user_id"})}
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    /**
     * Unique identifier for the category.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
/**
     * Name of the category (e.g., Salary, Food).
     */
    @Column(nullable = false)
    private String name;

    /**
     * Type of the category: INCOME or EXPENSE.
     */
    @Column(nullable = false)
    private String type; // INCOME or EXPENSE

    @Column(nullable = false)
    @JsonProperty("custom")
    private boolean isCustom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true) // ✅ allow null for default categories
    private User user;
}
