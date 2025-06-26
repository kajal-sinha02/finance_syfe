package com.finance.finance.service;

import com.finance.finance.dto.request.SavingsGoalRequest;
import com.finance.finance.dto.request.SavingsGoalUpdateRequest;
import com.finance.finance.dto.response.SavingsGoalResponse;
import com.finance.finance.entity.User;

import java.util.List;

public interface SavingsGoalService {
    SavingsGoalResponse createGoal(User user, SavingsGoalRequest request);
    List<SavingsGoalResponse> getAllGoals(User user);
    SavingsGoalResponse getGoalById(User user, Long id);
    SavingsGoalResponse updateGoal(User user, Long id, SavingsGoalUpdateRequest request);
    void deleteGoal(User user, Long id);
}