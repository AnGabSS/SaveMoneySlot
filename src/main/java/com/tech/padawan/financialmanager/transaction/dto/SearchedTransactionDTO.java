package com.tech.padawan.financialmanager.transaction.dto;

import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Date;

public record SearchedTransactionDTO(
        Long id,
        String description,
        BigDecimal value,
        String category,
        Date createdAt,
        String user
) {

    public static SearchedTransactionDTO from(Transaction transaction) {
        return from(transaction, true);
    }

    public static SearchedTransactionDTO from(Transaction transaction, boolean includeUserUrl) {
        Long userId = transaction.getUser() != null ? transaction.getUser().getId() : null;
        Long categoryId = transaction.getCategory() != null ? transaction.getCategory().getId() : null;

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

        String categoryUrl = null;
        if (includeUserUrl && userId != null) {
            try {
                categoryUrl = ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("/transaction/category/{id}")
                        .buildAndExpand(userId)
                        .toUriString();
            } catch (IllegalStateException e) {
                categoryUrl = null;
            }
        }

        return new SearchedTransactionDTO(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getValue(),
                categoryUrl,
                transaction.getCreatedAt(),
                userUrl
        );
    }
}
