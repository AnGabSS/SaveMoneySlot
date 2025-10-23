package com.tech.padawan.financialmanager.goal.dto;

import com.tech.padawan.financialmanager.goal.model.RecurrencePeriod;
import com.tech.padawan.financialmanager.goal.model.SpendingLimitGoalType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public record UpdateSpendingLimitGoalDTO(

        @NotBlank(message = "Name cannot be blank")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,
        String reason,
        SpendingLimitGoalType limitType,
        @DecimalMin(value = "0.0", inclusive = false, message = "Limit amount must be greater than zero")
        BigDecimal limitAmount,
        @DecimalMin(value = "0.0", inclusive = false, message = "Percentage must be greater than zero")
        @DecimalMax(value = "100.0", message = "Percentage cannot be greater than 100")
        BigDecimal limitPercentage,
        LocalDate initialDate,
        LocalDate finalDate
) {}
