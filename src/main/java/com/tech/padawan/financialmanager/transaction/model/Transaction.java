package com.tech.padawan.financialmanager.transaction.model;

import com.tech.padawan.financialmanager.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @NotNull(message = "Value is required")
    @Min(value = 0, message = "Value be a negative number")
    private BigDecimal value;
    @NotBlank(message = "Description is required")
    private String description;
    private Date createdAt;

    @ManyToOne
    @NotNull(message = "Category is required")
    private TransactionCategory category;

    @ManyToOne
    @NotNull(message = "User is required")
    private User user;

}
