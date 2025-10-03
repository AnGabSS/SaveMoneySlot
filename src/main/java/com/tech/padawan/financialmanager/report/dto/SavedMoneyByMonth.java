package com.tech.padawan.financialmanager.report.dto;

import java.math.BigDecimal;

public record SavedMoneyByMonth(
        String month,
        BigDecimal savedMoney
) {
}
