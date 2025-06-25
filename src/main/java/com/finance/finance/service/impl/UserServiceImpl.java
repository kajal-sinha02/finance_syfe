// package com.finance.finance.service.impl;

// import com.finance.finance.dto.request.UserRegisterRequest;
// import com.finance.finance.dto.request.UserLoginRequest;
// import com.finance.finance.entity.User;
// import com.finance.finance.repository.UserRepository;
// import com.finance.finance.exception.UnauthorizedException;
// import com.finance.finance.service.UserService;

// import jakarta.servlet.http.HttpSession;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.stereotype.Service;

// @Service
// public class UserServiceImpl implements UserService {

//     private final UserRepository userRepository;
//     private final PasswordEncoder passwordEncoder;

//     @Autowired
//     public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
//         this.userRepository = userRepository;
//         this.passwordEncoder = passwordEncoder;
//     }

//     @Override
//     public User register(UserRegisterRequest request) {
//         User user = new User();
//         user.setUsername(request.getUsername());
//         user.setPassword(passwordEncoder.encode(request.getPassword()));
//         user.setFullName(request.getFullName());
//         user.setPhoneNumber(request.getPhoneNumber());
//         return userRepository.save(user);
//     }

//     @Override
//     public void login(UserLoginRequest request, HttpSession session) {
//         User user = userRepository.findByUsername(request.getUsername())
//                 .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

//         if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
//             throw new UnauthorizedException("Invalid credentials");
//         }

//         session.setAttribute("userId", user.getId());
//     }

//     @Override
//     public void logout(HttpSession session) {
//         session.invalidate();
//     }
// }
package com.finance.finance.service.impl;

import com.finance.finance.dto.request.UserRegisterRequest;
import com.finance.finance.dto.request.UserLoginRequest;
import com.finance.finance.entity.User;
import com.finance.finance.repository.UserRepository;
import com.finance.finance.exception.UnauthorizedException;
import com.finance.finance.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public User register(UserRegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());  // assuming username is email
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        return userRepository.save(user);
    }

    @Override
    public void login(UserLoginRequest request, HttpSession session) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(), // assuming this is email
                request.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
         User user = userRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new RuntimeException("User not found"));
    session.setAttribute("user", user);
    }

    @Override
    public void logout(HttpSession session) {
        session.invalidate();
    }
}
