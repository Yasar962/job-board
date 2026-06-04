package com.job_board.user_sevice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Enter the E-Mail")
    @Email(message = "Enter a Valid E-Mail")
    private String email;

    @NotBlank(message = "Enter the password")
    private String password;
}
