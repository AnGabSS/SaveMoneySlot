package com.tech.padawan.financialmanager.transaction.dto;

import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public record SearchedTransactionCategoryDTO(
        Long id,
        String name,
        TransactionType type,
        String userURL
) {

    public static SearchedTransactionCategoryDTO from(TransactionCategory category) {
        return from(category, true);
    }

    public static SearchedTransactionCategoryDTO from(TransactionCategory category, boolean includeUserUrl) {
        Long userId = category.getUser() != null ? category.getUser().getId() : null;

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

        return new SearchedTransactionCategoryDTO(
                category.getId(),
                category.getName(),
                category.getType(),
                userUrl
        );
    }

}
