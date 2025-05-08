package com.tech.padawan.financialmanager.user.dto;

import com.tech.padawan.financialmanager.role.model.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record UpdateUserDTO (
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Email is required")
        String email,
        @NotNull(message = "Birthdate is required")
        Date birthdate
){
}
