
package com.finance.finance.service;

import com.finance.finance.dto.request.UserLoginRequest;
import com.finance.finance.dto.request.UserRegisterRequest;
import com.finance.finance.entity.User;

import jakarta.servlet.http.HttpSession;

/**
 * Service interface for managing user authentication.
 */
public interface UserService {
    // register
    User register(UserRegisterRequest request);

    // login
    void login(UserLoginRequest request, HttpSession session);

    // logout
    void logout(HttpSession session);
}