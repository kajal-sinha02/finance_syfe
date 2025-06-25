package com.finance.finance.service;

import com.finance.finance.dto.request.SavingsGoalRequest;
import com.finance.finance.dto.response.SavingsGoalResponse;

import java.util.List;

public interface SavingsGoalService {
    SavingsGoalResponse createGoal(Long userId, SavingsGoalRequest request);
    List<SavingsGoalResponse> getAllGoalResponsesForUser(Long userId);
    SavingsGoalResponse updateGoal(Long userId, Long goalId, SavingsGoalRequest request);
    boolean deleteGoal(Long userId, Long goalId);
    SavingsGoalResponse getGoalResponseById(Long goalId);  // Add this to match controller
}
