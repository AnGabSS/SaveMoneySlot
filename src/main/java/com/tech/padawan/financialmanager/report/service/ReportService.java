package com.tech.padawan.financialmanager.report.service;

import com.tech.padawan.financialmanager.report.dto.SavedMoneyByMonth;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import com.tech.padawan.financialmanager.transaction.service.ITransactionService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService implements IReportService {

    private final ITransactionService transactionService;

    public ReportService(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public List<SavedMoneyByMonth> getSavedMoneyByMonth(Long userId, Date initialDate, Date finalDate) {
        List<Transaction> transactions = transactionService.findAllByUserAndMonth(userId, initialDate, finalDate);

        Map<YearMonth, BigDecimal> savedMoneyByMonthMap = transactions.stream()
                .collect(Collectors.groupingBy(
                        transaction -> YearMonth.from(transaction.getCreatedAt().toInstant()
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
}