package com.finance.finance.service.impl;

import com.finance.finance.dto.request.SavingsGoalRequest;
import com.finance.finance.dto.response.SavingsGoalResponse;
import com.finance.finance.entity.SavingsGoal;
import com.finance.finance.entity.User;
import com.finance.finance.exception.BadRequestException;
import com.finance.finance.exception.ResourceNotFoundException;
import com.finance.finance.repository.SavingsGoalRepository;
import com.finance.finance.repository.TransactionRepository;
import com.finance.finance.repository.UserRepository;
import com.finance.finance.service.SavingsGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavingsGoalServiceImpl implements SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public SavingsGoalResponse createGoal(Long userId, SavingsGoalRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validateRequest(request);

        SavingsGoal goal = new SavingsGoal();
        goal.setGoalName(request.getGoalName());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(request.getTargetDate());
        goal.setStartDate(LocalDate.now());
        goal.setUser(user);

        return mapToResponse(savingsGoalRepository.save(goal));
    }

    @Override
    public List<SavingsGoalResponse> getAllGoalResponsesForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return savingsGoalRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SavingsGoalResponse updateGoal(Long userId, Long goalId, SavingsGoalRequest request) {
        SavingsGoal goal = savingsGoalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (!goal.getUser().getId().equals(userId)) {
            throw new BadRequestException("You are not authorized to update this goal");
        }

        validateRequest(request);

        goal.setGoalName(request.getGoalName());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(request.getTargetDate());

        return mapToResponse(savingsGoalRepository.save(goal));
    }

    @Override
    public boolean deleteGoal(Long userId, Long goalId) {
        SavingsGoal goal = savingsGoalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (!goal.getUser().getId().equals(userId)) {
            throw new BadRequestException("You are not authorized to delete this goal");
        }

        savingsGoalRepository.delete(goal);
        return true;
    }

    @Override
    public SavingsGoalResponse getGoalResponseById(Long goalId) {
        SavingsGoal goal = savingsGoalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
        return mapToResponse(goal);
    }

    private void validateRequest(SavingsGoalRequest request) {
        if (request.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Target amount must be positive");
        }

        if (request.getTargetDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Target date must be in the future");
        }
    }
    private SavingsGoalResponse mapToResponse(SavingsGoal goal) {
    // Fetch income and expenses since the goal's start date
    BigDecimal income = transactionRepository
        .getSumAmountByUserAndTypeAndDateAfter(goal.getUser(), "INCOME", goal.getStartDate())
        .orElse(BigDecimal.ZERO);

    BigDecimal expense = transactionRepository
        .getSumAmountByUserAndTypeAndDateAfter(goal.getUser(), "EXPENSE", goal.getStartDate())
        .orElse(BigDecimal.ZERO);

    // Progress and remaining calculations
    BigDecimal progress = income.subtract(expense);
    BigDecimal targetAmount = goal.getTargetAmount();
    BigDecimal remaining = targetAmount.subtract(progress);

    double percentage = BigDecimal.ZERO.doubleValue();
    if (targetAmount.compareTo(BigDecimal.ZERO) > 0) {
        percentage = progress.multiply(BigDecimal.valueOf(100))
                .divide(targetAmount, 2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    // Build response
    SavingsGoalResponse response = new SavingsGoalResponse();
    response.setId(goal.getId());
    response.setGoalName(goal.getGoalName());
    response.setTargetAmount(goal.getTargetAmount());
    response.setTargetDate(goal.getTargetDate());
    response.setStartDate(goal.getStartDate());
    response.setProgressAmount(progress.max(BigDecimal.ZERO));
    response.setRemainingAmount(remaining.max(BigDecimal.ZERO));
    response.setCompletionPercentage(Math.min(100.0, Math.max(0.0, percentage)));

    return response;
}


//     private SavingsGoalResponse mapToResponse(SavingsGoal goal) {
//         BigDecimal income = transactionRepository
//     .getSumAmountByUserAndTypeAndDateAfter(goal.getUser(), "INCOME", goal.getStartDate())
//     .map(BigDecimal::valueOf)
//     .orElse(BigDecimal.ZERO);

// BigDecimal expense = transactionRepository
//     .getSumAmountByUserAndTypeAndDateAfter(goal.getUser(), "EXPENSE", goal.getStartDate())
//     .map(BigDecimal::valueOf)
//     .orElse(BigDecimal.ZERO);

//         BigDecimal progress = income.subtract(expense);
//         BigDecimal remaining = goal.getTargetAmount().subtract(progress);
//         double percentage = goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0
//                 ? progress.multiply(BigDecimal.valueOf(100)).divide(goal.getTargetAmount(), 2, BigDecimal.ROUND_HALF_UP).doubleValue()
//                 : 0.0;

//         SavingsGoalResponse response = new SavingsGoalResponse();
//         response.setId(goal.getId());
//         response.setGoalName(goal.getGoalName());
//         response.setTargetAmount(goal.getTargetAmount());
//         response.setTargetDate(goal.getTargetDate());
//         response.setStartDate(goal.getStartDate());
//         response.setProgressAmount(progress.max(BigDecimal.ZERO));
//         response.setRemainingAmount(remaining.max(BigDecimal.ZERO));
//         response.setCompletionPercentage(Math.min(100.0, Math.max(0.0, percentage)));

//         return response;
//     }
}
