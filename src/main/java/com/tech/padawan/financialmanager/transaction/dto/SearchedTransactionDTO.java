package com.tech.padawan.financialmanager.transaction.dto;

import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;

public record SearchedTransactionDTO(
        Long id,
        String description,
        double value,
        TransactionType type,
        Date createdAt,
        String user
) {

    public static SearchedTransactionDTO from(Transaction transaction) {
        return from(transaction, true);
    }

    public static SearchedTransactionDTO from(Transaction transaction, boolean includeUserUrl) {
        Long userId = transaction.getUser() != null ? transaction.getUser().getId() : null;

        String userUrl = null;
        if (includeUserUrl && userId != null) {
            try {
                userUrl = ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("/user/{id}")
                        .buildAndExpand(userId)
                        .toUriString();
            } catch (IllegalStateException e) {
                userUrl = null;
            }
        }

        return new SearchedTransactionDTO(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getValue(),
                transaction.getType(),
                transaction.getCreatedAt(),
                userUrl
        );
    }
}
