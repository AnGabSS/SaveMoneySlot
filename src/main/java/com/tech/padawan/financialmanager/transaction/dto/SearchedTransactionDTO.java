package com.tech.padawan.financialmanager.transaction.dto;

import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

public record SearchedTransactionDTO(
        Long id,
        String description,
        BigDecimal value,
        SearchedTransactionCategoryDTO category,
        LocalDateTime createdAt,
        String party
) {

    public static SearchedTransactionDTO from(Transaction transaction) {
        return from(transaction, true);
    }

    public static SearchedTransactionDTO from(Transaction transaction, boolean includePartyUrl) {
        Long partyId = transaction.getParty() != null ? transaction.getParty().getId() : null;

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


        return new SearchedTransactionDTO(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getValue(),
                SearchedTransactionCategoryDTO.from(transaction.getCategory()),
                transaction.getCreatedAt(),
                partyUrl
        );
    }
}
