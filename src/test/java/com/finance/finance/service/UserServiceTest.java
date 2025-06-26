package com.finance.finance.service;

import com.finance.finance.dto.request.UserLoginRequest;
import com.finance.finance.dto.request.UserRegisterRequest;
import com.finance.finance.entity.User;
import com.finance.finance.repository.UserRepository;
import com.finance.finance.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserServiceImpl.
 */
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private HttpSession session;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setUsername("test@example.com");
        user.setPassword("encodedPass");
        user.setFullName("Test User");
        user.setPhoneNumber("1234567890");
    }

    @Test
    void testRegister_success() {
        UserRegisterRequest request = new UserRegisterRequest(
                "test@example.com",
                "plainPassword",
                "Test User",
                "1234567890"
        );

        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User registeredUser = userService.register(request);

        assertEquals("test@example.com", registeredUser.getUsername());
        assertEquals("encodedPass", registeredUser.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testLogin_success() {
        UserLoginRequest request = new UserLoginRequest("test@example.com", "password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(user));

        userService.login(request, session);

        verify(session, times(1)).setAttribute(eq("SPRING_SECURITY_CONTEXT"), any());
        verify(session, times(1)).setAttribute("user", user);
    }

    @Test
    void testLogout_success() {
        userService.logout(session);
        verify(session, times(1)).invalidate();
    }
}
