package com.tech.padawan.financialmanager.goal.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("SAVING")
@Table(name = "saving_goals")
@PrimaryKeyJoinColumn(name = "goal_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavingGoal extends Goal {

    @NotNull(message = "Target Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Target must be greater than zero")
    private BigDecimal targetAmount;

    @NotNull(message = "Saved Amount is required")
    @DecimalMin(value = "0.0", message = "Saved amount cannot be negative")
    private BigDecimal savedAmount;

    private LocalDateTime createdAt;

}