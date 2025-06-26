package com.finance.finance.service;

import com.finance.finance.dto.request.SavingsGoalRequest;
import com.finance.finance.dto.request.SavingsGoalUpdateRequest;
import com.finance.finance.dto.response.SavingsGoalResponse;
import com.finance.finance.entity.User;

import java.util.List;

/**
 * Service interface for saving goal services.
 */

public interface SavingsGoalService {

    // create
    SavingsGoalResponse createGoal(User user, SavingsGoalRequest request);

    // gets all
    List<SavingsGoalResponse> getAllGoals(User user);

    // get by goal id
    SavingsGoalResponse getGoalById(User user, Long id);

    // update goal
    SavingsGoalResponse updateGoal(User user, Long id, SavingsGoalUpdateRequest request);

    // delete goal
    void deleteGoal(User user, Long id);
}