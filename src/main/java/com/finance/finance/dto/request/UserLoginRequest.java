package com.finance.finance.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import lombok.AllArgsConstructor;

@Getter
@Setter

@AllArgsConstructor 
// user login request dto
public class UserLoginRequest {
    @Email
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
