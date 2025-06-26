// Repository
package com.finance.finance.repository;

import com.finance.finance.entity.SavingsGoal;
import com.finance.finance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for performing CRUD operations on goals entities.
 */
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    // find user goal
    List<SavingsGoal> findByUser(User user);
    // find by id and user
    Optional<SavingsGoal> findByIdAndUser(Long id, User user);
}
