package com.job_board.user_sevice.dto;

import com.job_board.user_sevice.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "E-Mail is Required")
    @Email(message = "E-Mail must be valid")
    private String email;

    @NotBlank(message = "Password is Required")
    @Size(min = 6,message = "Password should have atleast 6 Letters")
    private String password;

    @NotBlank(message = "Full Name is Required")
    private String fullName;

    @NotNull(message = "Role is required")
    private Role role;
}
