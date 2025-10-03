package com.tech.padawan.financialmanager.report.service;

import com.tech.padawan.financialmanager.report.dto.SavedMoneyByMonth;
import com.tech.padawan.financialmanager.report.dto.TransactionCountByCategoryDTO;
import com.tech.padawan.financialmanager.report.dto.TransactionCountByTypeDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import com.tech.padawan.financialmanager.transaction.service.ITransactionService;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService implements IReportService {

    private final ITransactionService transactionService;

    public ReportService(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public List<SavedMoneyByMonth> getSavedMoneyByMonth(String email, LocalDateTime initialDate, LocalDateTime finalDate) {
        List<Transaction> transactions = transactionService.findAllByUserEmailAndMonth(email, initialDate, finalDate);

        Map<YearMonth, BigDecimal> savedMoneyByMonthMap = transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> YearMonth.from(transaction.getCreatedAt()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                transaction -> transaction.getCategory().getType() == TransactionType.INCOME
                                        ? transaction.getValue()
                                        : transaction.getValue().negate(),
                                BigDecimal::add
                        )
                ));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        return savedMoneyByMonthMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new SavedMoneyByMonth(
                        entry.getKey().format(formatter),
                        entry.getValue()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionCountByTypeDTO> getMonthlyTransactionCountGroupedByType(String email) {
        YearMonth yearMonth = YearMonth.now();
        LocalDateTime firstDayOfMonth = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime lastDayOfMonth = yearMonth.atEndOfMonth().atTime(LocalTime.MAX);

        List<Transaction> transactions = transactionService.findAllByUserEmailAndMonth(email, firstDayOfMonth, lastDayOfMonth);

        Map<TransactionType, Map<String, BigDecimal>> groupedAmounts = transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> transaction.getCategory().getType(),
                        Collectors.groupingBy(
                                transaction -> transaction.getCategory().getName(),
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        Transaction::getValue,
                                        BigDecimal::add
                                )
                        )
                ));

        return groupedAmounts.entrySet().stream()
                .map(entryByType -> {
                    TransactionType type = entryByType.getKey();
                    Map<String, BigDecimal> amountsByCategory = entryByType.getValue();

                    List<TransactionCountByCategoryDTO> categoryList = amountsByCategory.entrySet().stream()
                            .map(entryByCategory -> new TransactionCountByCategoryDTO(
                                    entryByCategory.getKey(),
                                    entryByCategory.getValue() // O valor agora é um BigDecimal
                            ))
                            .toList();

                    return new TransactionCountByTypeDTO(type, categoryList);
                })
                .toList();
    }
}