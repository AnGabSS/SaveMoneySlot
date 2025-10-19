package com.tech.padawan.financialmanager.party.model;

import com.tech.padawan.financialmanager.champion.model.Champion;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="parties")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Points is required")
    @Min(value = 0, message = "Value be a negative number")
    private int points;

    @Setter
    @ColumnDefault("0")
    private BigDecimal balance;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "party", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Transaction> transactions;

    @OneToMany(mappedBy = "party", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TransactionCategory> transactionsCategories;

    @Setter
    @ManyToMany(fetch = FetchType.LAZY)
    @NotNull(message = "Champions is required")
    private List<Champion> champions;
}
