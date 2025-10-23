package com.tech.padawan.financialmanager.report.service;

import com.tech.padawan.financialmanager.report.dto.SavedMoneyByMonth;
import com.tech.padawan.financialmanager.report.dto.TransactionCountByTypeDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface IReportService {
    List<SavedMoneyByMonth> getSavedMoneyByMonth(Long id, LocalDateTime initialDate, LocalDateTime finalDate);
    List<TransactionCountByTypeDTO> getMonthlyTransactionCountGroupedByType(Long id);
    BigDecimal getTheMonthlyAverageValuesByType(Long partyId, TransactionType type);
    BigDecimal getTheTotalValuesByTypeBetweenDate(Long partyId, LocalDateTime initialDate, LocalDateTime finalDate, TransactionType type);
    BigDecimal getTheTotalValuesByCategoryBetweenDate(Long partyId, LocalDateTime initialDate, LocalDateTime finalDate, TransactionCategory category);
}
