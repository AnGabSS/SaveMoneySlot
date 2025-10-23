package com.tech.padawan.financialmanager.transaction.service;

import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.party.service.IPartyService;
import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.SearchedTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import com.tech.padawan.financialmanager.transaction.repository.TransactionRepository;
import com.tech.padawan.financialmanager.transaction.service.exception.TransactionNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;
    @Mock
    private IPartyService partyService; // Dependência ajustada
    @Mock
    private ITransactionBalanceService balanceService;
    @Mock
    private ITransactionCategoryService categoryService;

    @InjectMocks
    private TransactionService service;

    private Party mockParty;
    private TransactionCategory mockCategory;
    private Transaction mockTransaction;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mockParty = Party.builder().id(1L).name("Test Party").build();

        mockCategory = new TransactionCategory();
        mockCategory.setId(1L);
        mockCategory.setName("Salary");
        mockCategory.setType(TransactionType.INCOME);
        mockCategory.setParty(mockParty);

        mockTransaction = new Transaction();
        mockTransaction.setId(1L);
        mockTransaction.setValue(new BigDecimal("5000.00"));
        mockTransaction.setDescription("Monthly Salary");
        mockTransaction.setCategory(mockCategory);
        mockTransaction.setParty(mockParty);
        mockTransaction.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should return all transactions for a party, paginated")
    void shouldFindAllByPartyId() {
        Page<Transaction> page = new PageImpl<>(List.of(mockTransaction));
        when(repository.findAllByPartyIdAndDescriptionContainingIgnoreCase(any(PageRequest.class), eq(1L), eq("")))
                .thenReturn(page);

        Page<SearchedTransactionDTO> result = service.findAllByPartyId(1L, "", 1, 10, "id", "ASC");

        assertEquals(1, result.getTotalElements());
        verify(repository).findAllByPartyIdAndDescriptionContainingIgnoreCase(any(PageRequest.class), eq(1L), eq(""));
    }

    @Test
    @DisplayName("Should return transaction by ID")
    void shouldGetById() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockTransaction));
        SearchedTransactionDTO dto = service.getById(1L);
        assertEquals(mockTransaction.getDescription(), dto.description());
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when transaction not found")
    void shouldThrowWhenGetByIdNotFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(TransactionNotFound.class, () -> service.getById(2L));
    }

    @Test
    @DisplayName("Should create a transaction and update party balance")
    void shouldCreateTransaction() {
        CreateTransactionDTO dto = new CreateTransactionDTO(1L, new BigDecimal("5000.00"), "Salary", 1L);

        when(partyService.getById(1L)).thenReturn(mockParty);
        when(categoryService.getEntityById(1L)).thenReturn(mockCategory);
        when(balanceService.applyTransaction(mockParty, dto.value(), mockCategory.getType())).thenReturn(mockParty);
        when(partyService.updateCompleted(mockParty)).thenReturn(mockParty);
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = service.create(dto);

        assertNotNull(result);
        assertEquals(dto.value(), result.getValue());
        assertEquals(dto.description(), result.getDescription());
        assertEquals(mockParty, result.getParty());

        verify(partyService).getById(1L);
        verify(categoryService).getEntityById(1L);
        verify(balanceService).applyTransaction(mockParty, dto.value(), mockCategory.getType());
        verify(partyService).updateCompleted(mockParty);
        verify(repository).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Should update a transaction and correctly revert and apply balance changes")
    void shouldUpdateTransaction() {
        UpdateTransactionDTO dto = new UpdateTransactionDTO(new BigDecimal("5500.00"), "Salary + Bonus", 1L);

        // Mocking the service calls for the update flow
        when(repository.getReferenceById(1L)).thenReturn(mockTransaction);
        when(partyService.getById(1L)).thenReturn(mockParty);
        when(categoryService.getEntityById(1L)).thenReturn(mockCategory);
        when(balanceService.revertTransaction(any(Party.class), eq(mockTransaction.getValue()), eq(mockTransaction.getCategory().getType()))).thenReturn(mockParty);
        when(balanceService.applyTransaction(any(Party.class), eq(dto.value()), eq(mockCategory.getType()))).thenReturn(mockParty);
        when(partyService.updateCompleted(mockParty)).thenReturn(mockParty);
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.update(1L, dto);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(repository).save(captor.capture());
        Transaction savedTransaction = captor.getValue();

        assertEquals(dto.value(), savedTransaction.getValue());
        assertEquals(dto.description(), savedTransaction.getDescription());

        verify(balanceService).revertTransaction(any(Party.class), eq(new BigDecimal("5000.00")), any(TransactionType.class));
        verify(balanceService).applyTransaction(any(Party.class), eq(new BigDecimal("5500.00")), any(TransactionType.class));
    }

    @Test
    @DisplayName("Should delete a transaction")
    void shouldDeleteTransaction() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockTransaction));
        doNothing().when(repository).deleteById(1L);

        String result = service.delete(1L);

        assertEquals("Transaction deleted", result);
        verify(repository).findById(1L); // Called by getById inside delete
        verify(repository).deleteById(1L);
    }
}