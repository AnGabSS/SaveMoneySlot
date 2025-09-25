package com.tech.padawan.financialmanager.report.service;

import com.tech.padawan.financialmanager.report.dto.SavedMoneyByMonth;
import com.tech.padawan.financialmanager.report.dto.TransactionCountByTypeDTO;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface IReportService {
    List<SavedMoneyByMonth> getSavedMoneyByMonth(Long userId, LocalDateTime initialDate, LocalDateTime finalDate);
    List<TransactionCountByTypeDTO> getMonthlyTransactionCountGroupedByType(String email);
}
