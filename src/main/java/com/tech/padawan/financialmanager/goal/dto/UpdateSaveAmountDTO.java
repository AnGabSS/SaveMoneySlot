package com.tech.padawan.financialmanager.goal.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateSaveAmountDTO(
        @NotNull(message = "Value is required")
        @Min(value = 0, message = "Value must be positive")
        BigDecimal value
) {

}
