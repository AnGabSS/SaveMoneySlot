package com.tech.padawan.financialmanager.transaction.model;

import com.tech.padawan.financialmanager.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "transactions_categories")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @NotNull(message = "Name is required")
    private String name;
    @NotNull(message = "Type is required")
    private TransactionType type;
    @ManyToOne
    @NotEmpty(message = "User is required")
    private User user;
}
