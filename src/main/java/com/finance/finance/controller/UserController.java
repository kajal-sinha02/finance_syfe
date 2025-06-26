
package com.finance.finance.controller;

import com.finance.finance.dto.request.UserLoginRequest;
import com.finance.finance.dto.request.UserRegisterRequest;
import com.finance.finance.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import com.finance.finance.entity.User;

// controllers for user endpoints
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

   // register a new user
    @PostMapping("/register")
public ResponseEntity<Map<String, Object>> register(@RequestBody @Valid UserRegisterRequest request) {
    User user = userService.register(request); // returns User

    Map<String, Object> response = new HashMap<>();
    response.put("message", "User registered successfully");
    response.put("userId", user.getId());

    return ResponseEntity.status(201).body(response);
}

// login a new user

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginRequest request, HttpSession session) {
        userService.login(request, session);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Login successful");
        return ResponseEntity.ok(response);
    }

    // logout a new user
  @PostMapping("/logout")
public ResponseEntity<Map<String, String>> logout(HttpSession session) {
    userService.logout(session);
    Map<String, String> response = new HashMap<>();
    response.put("message", "Logout successful");
    return ResponseEntity.ok(response);
}
}
