package com.finance.finance.service;

import com.finance.finance.dto.request.SavingsGoalRequest;
import com.finance.finance.dto.request.SavingsGoalUpdateRequest;
import com.finance.finance.dto.response.SavingsGoalResponse;
import com.finance.finance.entity.SavingsGoal;
import com.finance.finance.entity.User;
import com.finance.finance.repository.SavingsGoalRepository;
import com.finance.finance.repository.TransactionRepository;
import com.finance.finance.service.impl.SavingsGoalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SavingsGoalServiceImplTest {

    @Mock
    private SavingsGoalRepository goalRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private SavingsGoalServiceImpl goalService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
    }

    @Test
    void createGoal_ValidRequest_ReturnsResponse() {
        SavingsGoalRequest request = new SavingsGoalRequest();
        request.setGoalName("Emergency Fund");
        request.setTargetAmount(BigDecimal.valueOf(10000));
        request.setTargetDate(LocalDate.now().plusMonths(3));
        request.setStartDate(LocalDate.now());

        SavingsGoal goal = SavingsGoal.builder()
                .id(1L)
                .goalName("Emergency Fund")
                .targetAmount(BigDecimal.valueOf(10000))
                .startDate(request.getStartDate())
                .targetDate(request.getTargetDate())
                .user(user)
                .build();

        when(goalRepository.save(any())).thenReturn(goal);
        when(transactionRepository.sumByUserAndDateAfter(user, request.getStartDate(), "INCOME"))
                .thenReturn(BigDecimal.valueOf(5000));
        when(transactionRepository.sumByUserAndDateAfter(user, request.getStartDate(), "EXPENSE"))
                .thenReturn(BigDecimal.valueOf(2000));

        SavingsGoalResponse response = goalService.createGoal(user, request);

        assertNotNull(response);
        assertEquals("Emergency Fund", response.getGoalName());
        assertEquals(BigDecimal.valueOf(3000.00).setScale(2), response.getCurrentProgress());
        assertEquals(BigDecimal.valueOf(7000.00).setScale(2), response.getRemainingAmount());
        assertEquals(BigDecimal.valueOf(30.0).setScale(1), response.getProgressPercentage());

        verify(goalRepository).save(any());
    }

    @Test
    void createGoal_InvalidAmount_ThrowsException() {
        SavingsGoalRequest request = new SavingsGoalRequest();
        request.setTargetAmount(BigDecimal.ZERO);
        request.setTargetDate(LocalDate.now().plusDays(10));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> goalService.createGoal(user, request));

        assertEquals("Target amount must be positive", ex.getReason());
    }

    @Test
    void createGoal_PastTargetDate_ThrowsException() {
        SavingsGoalRequest request = new SavingsGoalRequest();
        request.setTargetAmount(BigDecimal.valueOf(10000));
        request.setTargetDate(LocalDate.now().minusDays(1));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> goalService.createGoal(user, request));

        assertEquals("Target date must be in the future", ex.getReason());
    }

    @Test
    void createGoal_StartDateAfterTargetDate_ThrowsException() {
        SavingsGoalRequest request = new SavingsGoalRequest();
        request.setTargetAmount(BigDecimal.valueOf(10000));
        request.setTargetDate(LocalDate.now().plusDays(1));
        request.setStartDate(LocalDate.now().plusDays(5));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> goalService.createGoal(user, request));

        assertEquals("Start date cannot be after target date", ex.getReason());
    }

    @Test
    void getAllGoals_ReturnsList() {
        SavingsGoal goal = SavingsGoal.builder()
                .id(1L)
                .goalName("Trip")
                .targetAmount(BigDecimal.valueOf(5000))
                .startDate(LocalDate.now())
                .targetDate(LocalDate.now().plusMonths(2))
                .user(user)
                .build();

        when(goalRepository.findByUser(user)).thenReturn(Collections.singletonList(goal));
        when(transactionRepository.sumByUserAndDateAfter(user, goal.getStartDate(), "INCOME"))
                .thenReturn(BigDecimal.valueOf(3000));
        when(transactionRepository.sumByUserAndDateAfter(user, goal.getStartDate(), "EXPENSE"))
                .thenReturn(BigDecimal.valueOf(1000));

        var goals = goalService.getAllGoals(user);

        assertEquals(1, goals.size());
        assertEquals("Trip", goals.get(0).getGoalName());
    }

    @Test
    void getGoalById_Valid_ReturnsGoal() {
        SavingsGoal goal = SavingsGoal.builder()
                .id(1L)
                .goalName("Car")
                .targetAmount(BigDecimal.valueOf(20000))
                .startDate(LocalDate.now())
                .targetDate(LocalDate.now().plusMonths(6))
                .user(user)
                .build();

        when(goalRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(goal));
        when(transactionRepository.sumByUserAndDateAfter(user, goal.getStartDate(), "INCOME"))
                .thenReturn(BigDecimal.valueOf(7000));
        when(transactionRepository.sumByUserAndDateAfter(user, goal.getStartDate(), "EXPENSE"))
                .thenReturn(BigDecimal.valueOf(4000));

        SavingsGoalResponse response = goalService.getGoalById(user, 1L);

        assertEquals("Car", response.getGoalName());
        assertEquals(BigDecimal.valueOf(3000).setScale(2), response.getCurrentProgress());
    }

    @Test
    void getGoalById_NotFound_ThrowsException() {
        when(goalRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> goalService.getGoalById(user, 1L));

        assertEquals("Goal not found", ex.getReason());
    }

    @Test
    void updateGoal_ValidRequest_UpdatesAndReturnsResponse() {
        SavingsGoal goal = SavingsGoal.builder()
                .id(1L)
                .goalName("UpdateTest")
                .targetAmount(BigDecimal.valueOf(1000))
                .startDate(LocalDate.now())
                .targetDate(LocalDate.now().plusMonths(1))
                .user(user)
                .build();

        SavingsGoalUpdateRequest request = new SavingsGoalUpdateRequest();
        request.setTargetAmount(BigDecimal.valueOf(2000));
        request.setTargetDate(LocalDate.now().plusMonths(2));

        when(goalRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(goal));
        when(goalRepository.save(any())).thenReturn(goal);
        when(transactionRepository.sumByUserAndDateAfter(user, goal.getStartDate(), "INCOME")).thenReturn(BigDecimal.ZERO);
        when(transactionRepository.sumByUserAndDateAfter(user, goal.getStartDate(), "EXPENSE")).thenReturn(BigDecimal.ZERO);

        SavingsGoalResponse response = goalService.updateGoal(user, 1L, request);

        assertEquals(BigDecimal.valueOf(2000).setScale(2), response.getTargetAmount());
        assertEquals("UpdateTest", response.getGoalName());
    }

    @Test
    void deleteGoal_ValidRequest_Success() {
        SavingsGoal goal = SavingsGoal.builder()
                .id(1L)
                .goalName("ToDelete")
                .targetAmount(BigDecimal.valueOf(1000))
                .startDate(LocalDate.now())
                .targetDate(LocalDate.now().plusMonths(1))
                .user(user)
                .build();

        when(goalRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(goal));

        goalService.deleteGoal(user, 1L);

        verify(goalRepository).delete(goal);
    }

    @Test
    void deleteGoal_NotFound_ThrowsException() {
        when(goalRepository.findByIdAndUser(1L, user)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> goalService.deleteGoal(user, 1L));

        assertEquals("Goal not found", ex.getReason());
        verify(goalRepository, never()).delete(any());
    }
}
