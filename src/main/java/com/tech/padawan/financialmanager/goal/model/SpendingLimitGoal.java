package com.tech.padawan.financialmanager.goal.model;

import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("SPENDING_LIMIT")
@Table(name = "spending_limit_goals")
@PrimaryKeyJoinColumn(name = "goal_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpendingLimitGoal extends Goal {

    @NotNull(message = "Limit type is required")
    @Enumerated(EnumType.STRING)
    private SpendingLimitGoalType limitType;

    @DecimalMin(value = "0.0", inclusive = false, message = "Limit amount must be greater than zero")
    private BigDecimal limitAmount;

    @DecimalMin(value = "0.0", inclusive = false, message = "Percentage must be greater than zero")
    @DecimalMax(value = "100.0", message = "Percentage cannot be greater than 100")
    private BigDecimal limitPercentage;

    private LocalDateTime createdAt;

    @NotNull(message = "Initial Date is required")
    private LocalDate initialDate;
    @NotNull(message = "Final Date is required")
    private LocalDate finalDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @NotNull(message = "Category is required")
    private TransactionCategory category;
}