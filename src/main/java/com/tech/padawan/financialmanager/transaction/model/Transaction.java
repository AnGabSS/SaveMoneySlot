package com.tech.padawan.financialmanager.transaction.model;

import com.tech.padawan.financialmanager.user.model.User;
import jakarta.persistence.*;
import lombok.*;

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
    private double value;
    private String description;
    private TransactionType type;
    private Date createdAt;

    @ManyToOne
    private User user;

}
