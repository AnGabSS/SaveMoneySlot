package com.tech.padawan.financialmanager.goal.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateSavingGoalDTO(

        @NotBlank(message = "Name cannot be blank")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,
        String reason,
        @DecimalMin(value = "0.0", inclusive = false, message = "Target must be greater than zero")
        BigDecimal targetAmount
) {}