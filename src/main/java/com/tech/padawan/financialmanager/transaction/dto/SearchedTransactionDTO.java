package com.tech.padawan.financialmanager.transaction.dto;

import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import com.tech.padawan.financialmanager.user.dto.UserSearchedDTO;
import com.tech.padawan.financialmanager.user.model.User;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;

public record SearchedTransactionDTO(
        Long id,
        double value,
        String description,
        TransactionType type,
        Date createdAt,
        String user
) {
    public static SearchedTransactionDTO from(Transaction transaction) {
        Long userId = transaction.getUser() != null ? transaction.getUser().getId() : null;

        String userUrl = userId != null
                ? ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/user/{id}")
                .buildAndExpand(userId)
                .toUriString()
                : null;
        return new SearchedTransactionDTO(
                transaction.getId(),
                transaction.getValue(),
                transaction.getDescription(),
                transaction.getType(),
                transaction.getCreatedAt(),
                userUrl
        );
    }
}
