package com.finance.finance.service.impl;

import com.finance.finance.dto.request.SavingsGoalRequest;
import com.finance.finance.dto.request.SavingsGoalUpdateRequest;
import com.finance.finance.dto.response.SavingsGoalResponse;
import com.finance.finance.entity.SavingsGoal;
import com.finance.finance.entity.User;
import com.finance.finance.repository.SavingsGoalRepository;
import com.finance.finance.repository.TransactionRepository;
import com.finance.finance.service.SavingsGoalService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavingsGoalServiceImpl implements SavingsGoalService {

    private final SavingsGoalRepository goalRepository;
    private final TransactionRepository transactionRepository;

    public SavingsGoalServiceImpl(SavingsGoalRepository goalRepository, TransactionRepository transactionRepository) {
        this.goalRepository = goalRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public SavingsGoalResponse createGoal(User user, SavingsGoalRequest request) {
        if (request.getTargetAmount() == null || request.getTargetAmount().doubleValue() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target amount must be positive");
        }

        LocalDate targetDate = request.getTargetDate();
        if (targetDate == null || targetDate.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target date must be in the future");
        }

        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();

        if (startDate.isAfter(targetDate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start date cannot be after target date");
        }

        SavingsGoal goal = SavingsGoal.builder()
                .goalName(request.getGoalName())
                .targetAmount(request.getTargetAmount())
                .targetDate(targetDate)
                .startDate(startDate)
                .user(user)
                .build();

        goalRepository.save(goal);
        return mapToResponse(goal);
    }

    @Override
    public List<SavingsGoalResponse> getAllGoals(User user) {
        return goalRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SavingsGoalResponse getGoalById(User user, Long id) {
        SavingsGoal goal = goalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found"));
        return mapToResponse(goal);
    }

    @Override
    public SavingsGoalResponse updateGoal(User user, Long id, SavingsGoalUpdateRequest request) {
        SavingsGoal goal = goalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found"));

        if (request.getTargetAmount() != null && request.getTargetAmount().doubleValue() > 0) {
            goal.setTargetAmount(request.getTargetAmount());
        }

        if (request.getTargetDate() != null && request.getTargetDate().isAfter(LocalDate.now())) {
            goal.setTargetDate(request.getTargetDate());
        }

        goalRepository.save(goal);
        return mapToResponse(goal);
    }

    @Override
    public void deleteGoal(User user, Long id) {
        SavingsGoal goal = goalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Goal not found"));
        goalRepository.delete(goal);
    }

    private SavingsGoalResponse mapToResponse(SavingsGoal goal) {
    BigDecimal income = transactionRepository.sumByUserAndDateAfter(goal.getUser(), goal.getStartDate(), "INCOME");
    BigDecimal expense = transactionRepository.sumByUserAndDateAfter(goal.getUser(), goal.getStartDate(), "EXPENSE");

    // Ensure nulls are treated as zero
    income = income != null ? income : BigDecimal.ZERO;
    expense = expense != null ? expense : BigDecimal.ZERO;

    BigDecimal progress = income.subtract(expense).max(BigDecimal.ZERO);
    BigDecimal percentage;

    // Round percentage strictly to one decimal place using DOWN
    // if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
    //     percentage = progress
    //             .multiply(BigDecimal.valueOf(100))
    //             .divide(goal.getTargetAmount(), 5, RoundingMode.HALF_UP) // more precision before truncation
    //             .setScale(1, RoundingMode.DOWN); // exactly one decimal like 60.3, not 60.30
    // } else {
    //     percentage = BigDecimal.ZERO;
    // }

    
if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
    percentage = progress
            .multiply(BigDecimal.valueOf(100))
            .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP);
         
    // Format: remove trailing zeros and use minimum decimal places needed
    percentage = percentage.stripTrailingZeros();
    
    // Ensure at least 1 decimal place for consistency
    if (percentage.scale() < 1) {
        percentage = percentage.setScale(1, RoundingMode.HALF_UP);
    }
} else {
    // If target amount is 0 or negative, percentage is 0
    percentage = BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP);
}

    BigDecimal remaining = goal.getTargetAmount().subtract(progress).max(BigDecimal.ZERO);

    // Force strict equality: return 0 instead of 0.00 for zero values
    if (progress.compareTo(BigDecimal.ZERO) == 0) {
        progress = BigDecimal.ZERO;
    } else {
        progress = progress.setScale(2, RoundingMode.HALF_UP);
    }

    if (remaining.compareTo(BigDecimal.ZERO) == 0) {
        remaining = BigDecimal.ZERO;
    } else {
        remaining = remaining.setScale(2, RoundingMode.HALF_UP);
    }

    return SavingsGoalResponse.builder()
            .id(goal.getId())
            .goalName(goal.getGoalName())
            .targetAmount(goal.getTargetAmount().setScale(2, RoundingMode.HALF_UP))
            .targetDate(goal.getTargetDate().toString())
            .startDate(goal.getStartDate().toString())
            .currentProgress(progress)               // 0 if zero, else 2 decimal
            .progressPercentage(percentage)          // 0.0 if zero, else 1 decimal
            .remainingAmount(remaining)              // 0 if zero, else 2 decimal
            .build();
}


}