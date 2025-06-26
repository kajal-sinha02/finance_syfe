package com.finance.finance.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
// user response dto
public class UserResponse {
    private Long userId;
    private String username;
    private String fullName;
    private String phoneNumber;
}
