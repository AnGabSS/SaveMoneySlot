package com.tech.padawan.financialmanager.report.dto;

import com.tech.padawan.financialmanager.transaction.model.TransactionType;

import java.util.List;

public record TransactionCountByTypeDTO(
        TransactionType type,
        List<TransactionCountByCategoryDTO> transactionsQuantity
){

}
