/**
 * Represents a user of the Personal Finance Manager application.
 */
package com.finance.finance.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
     /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // email
    /**
     * Hashed password of the user.
     */
    @Column(nullable = false)
    private String password;
    /**
     * User's full name.
     */
    private String fullName;

    private String phoneNumber;
}
