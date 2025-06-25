package com.finance.finance.controller;

import com.finance.finance.dto.request.SavingsGoalRequest;
import com.finance.finance.dto.response.SavingsGoalResponse;
import com.finance.finance.service.SavingsGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    @PostMapping
    public ResponseEntity<SavingsGoalResponse> createGoal(
            @RequestBody SavingsGoalRequest request,
            Principal principal) {
        SavingsGoalResponse response = savingsGoalService.createGoal(request, principal);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SavingsGoalResponse>> getGoalsForUser(Principal principal) {
        List<SavingsGoalResponse> goals = savingsGoalService.getAllGoalResponsesForUser(principal);
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{goalId}")
    public ResponseEntity<SavingsGoalResponse> getGoalById(@PathVariable Long goalId) {
        SavingsGoalResponse response = savingsGoalService.getGoalResponseById(goalId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<SavingsGoalResponse> updateGoal(
            @PathVariable Long goalId,
            @RequestBody SavingsGoalRequest request,
            Principal principal) {
        SavingsGoalResponse response = savingsGoalService.updateGoal(goalId, request, principal);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Map<String, String>> deleteGoal(
        @PathVariable Long goalId,
        Principal principal) {
    boolean deleted = savingsGoalService.deleteGoal(goalId, principal);

    if (deleted) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Goal deleted successfully");
        return ResponseEntity.ok(response); // 200 OK with message
    } else {
        return ResponseEntity.notFound().build(); // 404 Not Found
    }
}
}
