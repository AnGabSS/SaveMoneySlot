package com.tech.padawan.financialmanager.goal.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record CreateGoalDTO(
        @NotBlank(message = "Name is required")
        String name,
        @NotNull(message = "Target Amount is required")
        @Min(value = 0, message = "Target cannot be a negative number")
        double targetAmount,
        @NotNull(message = "Saved Amount is required")
        double savedAmount,
        @Nullable
        String reason,
        @Nullable
        Date deadline,
        @NotNull(message = "UserId is required")
        @Min(value = 0, message = "UserId cannot be a negative number")
        Long userId
) {
}
