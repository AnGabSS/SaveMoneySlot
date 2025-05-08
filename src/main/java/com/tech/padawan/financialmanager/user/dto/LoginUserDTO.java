package com.tech.padawan.financialmanager.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginUserDTO(
        @NotBlank(message = "Email is required")
        String email,
        @NotBlank(message = "Password is required")
        String password
) {

}
