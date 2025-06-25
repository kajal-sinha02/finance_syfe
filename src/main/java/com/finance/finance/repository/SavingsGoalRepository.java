package com.finance.finance.repository;

import com.finance.finance.entity.SavingsGoal;
import com.finance.finance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUser(User user);
}
