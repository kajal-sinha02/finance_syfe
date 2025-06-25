package com.finance.finance.service;

import com.finance.finance.dto.request.SavingsGoalRequest;
import com.finance.finance.dto.response.SavingsGoalResponse;

import java.security.Principal;
import java.util.List;

public interface SavingsGoalService {

    SavingsGoalResponse createGoal(SavingsGoalRequest request, Principal principal);

    List<SavingsGoalResponse> getAllGoalResponsesForUser(Principal principal);

    SavingsGoalResponse updateGoal(Long goalId, SavingsGoalRequest request, Principal principal);

    boolean deleteGoal(Long goalId, Principal principal);

    SavingsGoalResponse getGoalResponseById(Long goalId);
}
