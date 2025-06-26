package com.finance.finance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finance.finance.dto.request.UserLoginRequest;
import com.finance.finance.dto.request.UserRegisterRequest;
import com.finance.finance.entity.User;
import com.finance.finance.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// ✅ Import SecurityConfig to mimic session behavior
@WebMvcTest(UserController.class)
@Import(com.finance.finance.config.SecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testRegister() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest(
                "test@example.com", "password123", "Test User", "+911234567890"
        );

        User mockUser = new User();
        mockUser.setId(1L);

        Mockito.when(userService.register(any(UserRegisterRequest.class))).thenReturn(mockUser);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void testLogin() throws Exception {
        UserLoginRequest request = new UserLoginRequest("test@example.com", "password123");

        // Simulate service storing user in session
        Mockito.doAnswer(invocation -> {
            HttpSession session = invocation.getArgument(1);
            session.setAttribute("user", new User());
            return null;
        }).when(userService).login(any(UserLoginRequest.class), any(HttpSession.class));

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/auth/login")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    void testLogout() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", new User());

        Mockito.doNothing().when(userService).logout(any(HttpSession.class));

        mockMvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Logout successful"));
    }
}
