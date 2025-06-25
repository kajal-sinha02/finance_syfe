package com.finance.finance.controller;

import com.finance.finance.dto.request.SavingsGoalRequest;
import com.finance.finance.dto.response.SavingsGoalResponse;
import com.finance.finance.service.SavingsGoalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    @Autowired
    public SavingsGoalController(SavingsGoalService savingsGoalService) {
        this.savingsGoalService = savingsGoalService;
    }

    // Create new goal for a user
    @PostMapping
    public ResponseEntity<SavingsGoalResponse> createGoal(
            @RequestParam Long userId,
            @RequestBody SavingsGoalRequest request) {
        SavingsGoalResponse response = savingsGoalService.createGoal(userId, request);
        return ResponseEntity.ok(response);
    }

    // Get goal by id
    @GetMapping("/{goalId}")
    public ResponseEntity<SavingsGoalResponse> getGoalById(@PathVariable Long goalId) {
        SavingsGoalResponse response = savingsGoalService.getGoalResponseById(goalId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    // Get all goals for a user
    @GetMapping
    public ResponseEntity<List<SavingsGoalResponse>> getGoalsForUser(@RequestParam Long userId) {
        List<SavingsGoalResponse> goals = savingsGoalService.getAllGoalResponsesForUser(userId);
        return ResponseEntity.ok(goals);
    }

    // Update a goal by id for a user
    @PutMapping("/{goalId}")
    public ResponseEntity<SavingsGoalResponse> updateGoal(
            @RequestParam Long userId,
            @PathVariable Long goalId,
            @RequestBody SavingsGoalRequest request) {
        SavingsGoalResponse response = savingsGoalService.updateGoal(userId, goalId, request);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    // Delete a goal by id for a user
    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(
            @RequestParam Long userId,
            @PathVariable Long goalId) {
        boolean deleted = savingsGoalService.deleteGoal(userId, goalId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
