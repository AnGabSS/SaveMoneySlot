package com.tech.padawan.financialmanager.transaction.dto;

import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public record SearchedTransactionCategoryDTO(
        Long id,
        String name,
        TransactionType type,
        String party
) {

    public static SearchedTransactionCategoryDTO from(TransactionCategory category) {
        return from(category, true);
    }

    public static SearchedTransactionCategoryDTO from(TransactionCategory category, boolean includePartyUrl) {
        Long partyId = category.getParty() != null ? category.getParty().getId() : null;

        String partyUrl = null;
        if (includePartyUrl && partyId != null) {
            try {
                partyUrl = ServletUriComponentsBuilder
                        .fromCurrentContextPath()
                        .path("/party/{id}")
                        .buildAndExpand(partyId)
                        .toUriString();
            } catch (IllegalStateException ignored) {
            }
        }

        return new SearchedTransactionCategoryDTO(
                category.getId(),
                category.getName(),
                category.getType(),
                partyUrl
        );
    }

}
