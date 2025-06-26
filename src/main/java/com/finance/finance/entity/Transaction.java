/**
 * Represents a financial transaction (income or expense).
 */
package com.finance.finance.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
// Constructors, getters, setters..
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private LocalDate date;

    private String description;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String type;       
}
