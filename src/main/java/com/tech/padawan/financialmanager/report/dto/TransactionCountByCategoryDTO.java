package com.tech.padawan.financialmanager.report.dto;

import java.math.BigDecimal;

public record TransactionCountByCategoryDTO(
        String category,
        BigDecimal amount
) {
}
