package com.tech.padawan.financialmanager.report.service;

import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.report.dto.SavedMoneyByMonth;
import com.tech.padawan.financialmanager.report.dto.TransactionCountByTypeDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import com.tech.padawan.financialmanager.transaction.service.ITransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ITransactionService transactionService;

    @InjectMocks
    private ReportService reportService;

    private Party testParty;
    private TransactionCategory salaryCategory;
    private TransactionCategory foodCategory;
    private TransactionCategory rentCategory;

    @BeforeEach
    void setUp() {
        // Party de teste criada para ser usada nas categorias e transações
        testParty = Party.builder().id(1L).build();

        // Categorias agora recebem a party no construtor
        salaryCategory = new TransactionCategory(1L, "Salário", TransactionType.INCOME, testParty);
        foodCategory = new TransactionCategory(2L, "Alimentação", TransactionType.EXPENSE, testParty);
        rentCategory = new TransactionCategory(3L, "Aluguel", TransactionType.EXPENSE, testParty);
    }

    @Test
    void getSavedMoneyByMonth_shouldGroupAndSumCorrectly() {
        // Arrange
        Long partyId = 1L;
        LocalDateTime oct1 = LocalDateTime.of(2025, 10, 5, 10, 0);
        LocalDateTime oct2 = LocalDateTime.of(2025, 10, 15, 12, 0);
        LocalDateTime sep1 = LocalDateTime.of(2025, 9, 20, 18, 0);

        List<Transaction> transactions = List.of(
                new Transaction(1L, new BigDecimal("2500.00"), "Salário Out", oct1, salaryCategory, testParty),
                new Transaction(2L, new BigDecimal("300.50"), "Mercado", oct2, foodCategory, testParty),
                new Transaction(3L, new BigDecimal("1200.00"), "Aluguel Set", sep1, rentCategory, testParty)
        );

        when(transactionService.findAllByPartyIdAndMonth(eq(partyId), any(), any())).thenReturn(transactions);

        // Act
        List<SavedMoneyByMonth> result = reportService.getSavedMoneyByMonth(partyId, null, null);

        // Assert
        assertEquals(2, result.size());
        assertEquals("09/2025", result.get(0).month());
        assertEquals(0, new BigDecimal("-1200.00").compareTo(result.get(0).savedMoney()));
        assertEquals("10/2025", result.get(1).month());
        assertEquals(0, new BigDecimal("2199.50").compareTo(result.get(1).savedMoney()));
    }

    @Test
    void getMonthlyTransactionCountGroupedByType_shouldGroupAndSumCategories() {
        // Arrange
        Long partyId = 1L;
        LocalDateTime now = LocalDateTime.now();
        TransactionCategory freelanceCategory = new TransactionCategory(4L, "Freelance", TransactionType.INCOME, testParty);

        List<Transaction> transactions = List.of(
                new Transaction(1L, new BigDecimal("5000.00"), "Salário", now, salaryCategory, testParty),
                new Transaction(2L, new BigDecimal("150.00"), "Almoço", now, foodCategory, testParty),
                new Transaction(3L, new BigDecimal("80.50"), "Café", now, foodCategory, testParty),
                new Transaction(4L, new BigDecimal("750.00"), "Projeto X", now, freelanceCategory, testParty)
        );

        when(transactionService.findAllByPartyIdAndMonth(eq(partyId), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(transactions);

        // Act
        List<TransactionCountByTypeDTO> result = reportService.getMonthlyTransactionCountGroupedByType(partyId);

        // Assert
        assertEquals(2, result.size());
        TransactionCountByTypeDTO incomeDto = result.stream().filter(r -> r.type() == TransactionType.INCOME).findFirst().orElseThrow();
        TransactionCountByTypeDTO expenseDto = result.stream().filter(r -> r.type() == TransactionType.EXPENSE).findFirst().orElseThrow();

        assertEquals(2, incomeDto.transactionsQuantity().size());
        assertEquals(0, new BigDecimal("5000.00").compareTo(incomeDto.transactionsQuantity().stream().filter(c -> c.category().equals("Salário")).findFirst().get().amount()));

        assertEquals(1, expenseDto.transactionsQuantity().size());
        assertEquals(0, new BigDecimal("230.50").compareTo(expenseDto.transactionsQuantity().get(0).amount()));
    }

    @Test
    void getTheMonthlyAverageValuesByType_shouldCalculateAverageCorrectly() {
        // Arrange
        Long partyId = 1L;
        List<Transaction> transactions = List.of(
                new Transaction(1L, new BigDecimal("2000"), "", LocalDateTime.of(2025, 10, 1, 0, 0), salaryCategory, testParty),
                new Transaction(2L, new BigDecimal("1000"), "", LocalDateTime.of(2025, 10, 1, 0, 0), salaryCategory, testParty),
                new Transaction(3L, new BigDecimal("2400"), "", LocalDateTime.of(2025, 9, 1, 0, 0), salaryCategory, testParty)
        );
        when(transactionService.findAllByPartyIdAndType(partyId, TransactionType.INCOME)).thenReturn(transactions);

        // Act
        BigDecimal average = reportService.getTheMonthlyAverageValuesByType(partyId, TransactionType.INCOME);

        // Assert
        BigDecimal expectedAverage = new BigDecimal("5400.00").divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
        assertEquals(0, expectedAverage.compareTo(average));
    }

    @Test
    void getTheMonthlyAverageValuesByType_shouldReturnZero_whenNoTransactions() {
        // Arrange
        when(transactionService.findAllByPartyIdAndType(anyLong(), any(TransactionType.class))).thenReturn(Collections.emptyList());

        // Act
        BigDecimal average = reportService.getTheMonthlyAverageValuesByType(1L, TransactionType.EXPENSE);

        // Assert
        assertEquals(0, BigDecimal.ZERO.compareTo(average));
    }

    @Test
    void getTheTotalValuesByTypeBetweenDate_shouldReturnCorrectSum() {
        // Arrange
        Long partyId = 1L;
        List<Transaction> transactions = List.of(
                new Transaction(1L, new BigDecimal("100.00"), "", null, foodCategory, testParty),
                new Transaction(2L, new BigDecimal("250.75"), "", null, rentCategory, testParty)
        );
        when(transactionService.findAllByPartyIdAndMonthAndType(anyLong(), any(), any(), eq(TransactionType.EXPENSE))).thenReturn(transactions);

        // Act
        BigDecimal total = reportService.getTheTotalValuesByTypeBetweenDate(partyId, null, null, TransactionType.EXPENSE);

        // Assert
        assertEquals(0, new BigDecimal("350.75").compareTo(total));
    }

    @Test
    void getTheTotalValuesByCategoryBetweenDate_shouldReturnCorrectSum() {
        // Arrange
        Long partyId = 1L;
        List<Transaction> transactions = List.of(
                new Transaction(1L, new BigDecimal("100.00"), "", null, foodCategory, testParty),
                new Transaction(2L, new BigDecimal("45.50"), "", null, foodCategory, testParty)
        );
        when(transactionService.findAllByPartyIdAndMonthAndCategory(anyLong(), any(), any(), eq(foodCategory))).thenReturn(transactions);

        // Act
        BigDecimal total = reportService.getTheTotalValuesByCategoryBetweenDate(partyId, null, null, foodCategory);

        // Assert
        assertEquals(0, new BigDecimal("145.50").compareTo(total));
    }
}