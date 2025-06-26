package com.finance.finance.controller;

import com.finance.finance.dto.request.SavingsGoalRequest;
import com.finance.finance.dto.request.SavingsGoalUpdateRequest;
import com.finance.finance.dto.response.SavingsGoalResponse;
import com.finance.finance.entity.User;
import com.finance.finance.service.SavingsGoalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SavingsGoalControllerTest {

    private MockMvc mockMvc;
    private SavingsGoalService savingsGoalService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private User mockUser;

    @BeforeEach
    void setUp() {
        // Configure ObjectMapper for LocalDate serialization
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        savingsGoalService = mock(SavingsGoalService.class);
        SavingsGoalController controller = new SavingsGoalController(savingsGoalService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // Setup mock user for session management
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("test@example.com");
        mockUser.setFullName("Test User");
    }

    @Test
    void testCreateGoal() throws Exception {
        SavingsGoalRequest request = new SavingsGoalRequest();
        request.setGoalName("Vacation Fund");
        request.setTargetAmount(BigDecimal.valueOf(1000));
        request.setTargetDate(LocalDate.now().plusMonths(6));
        request.setStartDate(LocalDate.now());

        SavingsGoalResponse response = SavingsGoalResponse.builder()
                .id(1L)
                .goalName("Vacation Fund")
                .targetAmount(BigDecimal.valueOf(1000))
                .targetDate(LocalDate.now().plusMonths(6).toString())
                .startDate(LocalDate.now().toString())
                .currentProgress(BigDecimal.ZERO)
                .progressPercentage(BigDecimal.ZERO)
                .remainingAmount(BigDecimal.valueOf(1000))
                .build();

        when(savingsGoalService.createGoal(any(User.class), any(SavingsGoalRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/goals")
                        .sessionAttr("user", mockUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.goalName").value("Vacation Fund"))
                .andExpect(jsonPath("$.targetAmount").value(1000))
                .andExpect(jsonPath("$.startDate").value(LocalDate.now().toString()))
                .andExpect(jsonPath("$.currentProgress").value(0))
                .andExpect(jsonPath("$.progressPercentage").value(0.0))
                .andExpect(jsonPath("$.remainingAmount").value(1000));
    }

    @Test
    void testGetAllGoals() throws Exception {
        SavingsGoalResponse response = SavingsGoalResponse.builder()
                .id(1L)
                .goalName("Car Fund")
                .targetAmount(BigDecimal.valueOf(5000))
                .targetDate(LocalDate.now().plusYears(1).toString())
                .startDate(LocalDate.now().toString())
                .currentProgress(BigDecimal.valueOf(1000))
                .progressPercentage(BigDecimal.valueOf(20.0))
                .remainingAmount(BigDecimal.valueOf(4000))
                .build();

        when(savingsGoalService.getAllGoals(eq(mockUser)))
                .thenReturn(Collections.singletonList(response));

        mockMvc.perform(get("/api/goals")
                        .sessionAttr("user", mockUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goals[0].goalName").value("Car Fund"))
                .andExpect(jsonPath("$.goals[0].targetAmount").value(5000))
                .andExpect(jsonPath("$.goals[0].currentProgress").value(1000))
                .andExpect(jsonPath("$.goals[0].progressPercentage").value(20.0))
                .andExpect(jsonPath("$.goals[0].remainingAmount").value(4000));
    }

    @Test
    void testGetGoalById() throws Exception {
        SavingsGoalResponse response = SavingsGoalResponse.builder()
                .id(1L)
                .goalName("Emergency Fund")
                .targetAmount(BigDecimal.valueOf(2000))
                .targetDate(LocalDate.now().plusMonths(3).toString())
                .startDate(LocalDate.now().toString())
                .currentProgress(BigDecimal.valueOf(500))
                .progressPercentage(BigDecimal.valueOf(25.0))
                .remainingAmount(BigDecimal.valueOf(1500))
                .build();

        when(savingsGoalService.getGoalById(eq(mockUser), eq(1L)))
                .thenReturn(response);

        mockMvc.perform(get("/api/goals/1")
                        .sessionAttr("user", mockUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalName").value("Emergency Fund"))
                .andExpect(jsonPath("$.targetAmount").value(2000))
                .andExpect(jsonPath("$.currentProgress").value(500))
                .andExpect(jsonPath("$.progressPercentage").value(25.0))
                .andExpect(jsonPath("$.remainingAmount").value(1500));
    }

    @Test
    void testUpdateGoal() throws Exception {
        SavingsGoalUpdateRequest updateRequest = new SavingsGoalUpdateRequest();
        updateRequest.setGoalName("Updated Vacation Fund");
        updateRequest.setTargetAmount(BigDecimal.valueOf(1500));
        updateRequest.setTargetDate(LocalDate.now().plusMonths(8));

        SavingsGoalResponse response = SavingsGoalResponse.builder()
                .id(1L)
                .goalName("Updated Vacation Fund")
                .targetAmount(BigDecimal.valueOf(1500))
                .targetDate(LocalDate.now().plusMonths(8).toString())
                .startDate(LocalDate.now().toString())
                .currentProgress(BigDecimal.valueOf(300))
                .progressPercentage(BigDecimal.valueOf(20.0))
                .remainingAmount(BigDecimal.valueOf(1200))
                .build();

        when(savingsGoalService.updateGoal(eq(mockUser), eq(1L), any(SavingsGoalUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/goals/1")
                        .sessionAttr("user", mockUser)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalName").value("Updated Vacation Fund"))
                .andExpect(jsonPath("$.targetAmount").value(1500))
                .andExpect(jsonPath("$.currentProgress").value(300))
                .andExpect(jsonPath("$.progressPercentage").value(20.0))
                .andExpect(jsonPath("$.remainingAmount").value(1200));
    }

    @Test
    void testDeleteGoal() throws Exception {
        doNothing().when(savingsGoalService).deleteGoal(mockUser, 1L);

        mockMvc.perform(delete("/api/goals/1")
                        .sessionAttr("user", mockUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Goal deleted successfully"));
    }
}