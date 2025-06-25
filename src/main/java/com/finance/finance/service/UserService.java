// package com.finance.finance.service;

// import com.finance.finance.dto.request.UserLoginRequest;
// import com.finance.finance.dto.request.UserRegisterRequest;
// import com.finance.finance.entity.User;

// import jakarta.servlet.http.HttpSession;

// public interface UserService {
//     User register(UserRegisterRequest request);
//     void login(UserLoginRequest request, HttpSession session);
//     void logout(HttpSession session);
// }
package com.finance.finance.service;

import com.finance.finance.dto.request.UserLoginRequest;
import com.finance.finance.dto.request.UserRegisterRequest;
import com.finance.finance.entity.User;

import jakarta.servlet.http.HttpSession;

public interface UserService {
    User register(UserRegisterRequest request);
    void login(UserLoginRequest request, HttpSession session);
    void logout(HttpSession session);
}