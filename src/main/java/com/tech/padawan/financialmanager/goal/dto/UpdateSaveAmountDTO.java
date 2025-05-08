package com.tech.padawan.financialmanager.goal.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateSaveAmountDTO(
        @NotNull(message = "Value is required")
        @Min(value = 0, message = "Value must be positive")
        Double value
) {

}
