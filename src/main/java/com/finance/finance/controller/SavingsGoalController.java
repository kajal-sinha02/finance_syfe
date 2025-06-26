package com.finance.finance.controller;

import com.finance.finance.dto.request.SavingsGoalRequest;
import com.finance.finance.dto.request.SavingsGoalUpdateRequest;
import com.finance.finance.dto.response.SavingsGoalResponse;
import com.finance.finance.entity.User;
import com.finance.finance.service.SavingsGoalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// controllers for saving goals endpoints
@RestController
@RequestMapping("/api/goals")
public class SavingsGoalController {

    private final SavingsGoalService goalService;

    public SavingsGoalController(SavingsGoalService goalService) {
        this.goalService = goalService;
    }

    // create goal endpoints
    @PostMapping
    public ResponseEntity<SavingsGoalResponse> createGoal(@SessionAttribute("user") User user,
                                                          @RequestBody SavingsGoalRequest request) {
        return new ResponseEntity<>(goalService.createGoal(user, request), HttpStatus.CREATED);
    }

    // get all goals
    @GetMapping
    public ResponseEntity<Map<String, List<SavingsGoalResponse>>> getAllGoals(@SessionAttribute("user") User user) {
        List<SavingsGoalResponse> goals = goalService.getAllGoals(user);
        Map<String, List<SavingsGoalResponse>> response = new HashMap<>();
        response.put("goals", goals);
        return ResponseEntity.ok(response);
    }

    //  get goals by id

    @GetMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> getGoalById(@SessionAttribute("user") User user,
                                                           @PathVariable Long id) {
        return ResponseEntity.ok(goalService.getGoalById(user, id));
    }

    // update goals
    @PutMapping("/{id}")
    public ResponseEntity<SavingsGoalResponse> updateGoal(@SessionAttribute("user") User user,
                                                          @PathVariable Long id,
                                                          @RequestBody SavingsGoalUpdateRequest request) {
        return ResponseEntity.ok(goalService.updateGoal(user, id, request));
    }

    // delete goals

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteGoal(@SessionAttribute("user") User user,
                                                          @PathVariable Long id) {
        goalService.deleteGoal(user, id);
        Map<String, String> res = new HashMap<>();
        res.put("message", "Goal deleted successfully");
        return ResponseEntity.ok(res);
    }
}