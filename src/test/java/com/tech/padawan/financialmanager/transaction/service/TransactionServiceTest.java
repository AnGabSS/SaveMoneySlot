package com.tech.padawan.financialmanager.transaction.service;

import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.SearchedTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import com.tech.padawan.financialmanager.transaction.repository.TransactionRepository;
import com.tech.padawan.financialmanager.transaction.service.exception.TransactionNotFound;
import com.tech.padawan.financialmanager.transaction.strategy.TransactionStrategy;
import com.tech.padawan.financialmanager.transaction.strategy.TransactionStrategyFactory;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionService service;

    private User mockUser;
    private Transaction mockTransaction;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockUser = User.builder().id(1L).name("User Test").build();

        mockTransaction = Transaction.builder()
                .id(1L)
                .value(100.0)
                .description("Salary")
                .type(TransactionType.INCOME)
                .createdAt(new Date())
                .user(mockUser)
                .build();
    }

    @Test
    @DisplayName("Should return all transactions paginated")
    void findAll() {
        Page<Transaction> page = new PageImpl<>(List.of(mockTransaction));
        when(repository.findAll(any(PageRequest.class))).thenReturn(page);

        Page<SearchedTransactionDTO> result = service.findAll(0, 10, "id", "ASC");

        assertEquals(1, result.getTotalElements());
        verify(repository).findAll(any(PageRequest.class));
    }

    @Test
    @DisplayName("Should return transaction by ID")
    void getById() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockTransaction));

        SearchedTransactionDTO dto = service.getById(1L);

        assertEquals(mockTransaction.getDescription(), dto.description());
        verify(repository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when transaction not found")
    void getById_NotFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFound.class, () -> service.getById(2L));
    }

    @Test
    @DisplayName("Should create a transaction")
    void create() {
        CreateTransactionDTO dto = new CreateTransactionDTO(
                200.0, "Freelance", TransactionType.INCOME, 1L
        );

        TransactionStrategy strategy = mock(TransactionStrategy.class);
        when(userService.getUserEntityById(1L)).thenReturn(mockUser);
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(strategy.apply(any(User.class), eq(200.0))).thenReturn(mockUser);

        try (MockedStatic<TransactionStrategyFactory> factory = mockStatic(TransactionStrategyFactory.class)) {
            factory.when(() -> TransactionStrategyFactory.getStrategy(TransactionType.INCOME)).thenReturn(strategy);

            Transaction created = service.create(dto);

            assertEquals(dto.description(), created.getDescription());
            verify(repository).save(any(Transaction.class));
        }
    }

    @Test
    @DisplayName("Should update a transaction")
    void update() {
        UpdateTransactionDTO dto = new UpdateTransactionDTO(150.0, "Updated", TransactionType.EXPENSE);
        TransactionStrategy oldStrategy = mock(TransactionStrategy.class);
        TransactionStrategy newStrategy = mock(TransactionStrategy.class);

        when(repository.getReferenceById(1L)).thenReturn(mockTransaction);
        when(userService.getUserEntityById(1L)).thenReturn(mockUser);
        when(oldStrategy.revert(any(User.class), eq(100.0))).thenReturn(mockUser);
        when(newStrategy.apply(any(User.class), eq(150.0))).thenReturn(mockUser);
        when(repository.save(any(Transaction.class))).thenReturn(mockTransaction);

        try (MockedStatic<TransactionStrategyFactory> factory = mockStatic(TransactionStrategyFactory.class)) {
            factory.when(() -> TransactionStrategyFactory.getStrategy(TransactionType.INCOME)).thenReturn(oldStrategy);
            factory.when(() -> TransactionStrategyFactory.getStrategy(TransactionType.EXPENSE)).thenReturn(newStrategy);

            SearchedTransactionDTO result = service.update(1L, dto);

            assertEquals(dto.description(), result.description());
            verify(repository).save(any(Transaction.class));
        }
    }

    @Test
    @DisplayName("Should delete a transaction")
    void delete() {
        when(repository.findById(1L)).thenReturn(Optional.of(mockTransaction));

        String result = service.delete(1L);

        assertEquals("Transaction deleted", result);
        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("Should return transactions by user ID paginated")
    void findAllByUser() {
        Page<Transaction> page = new PageImpl<>(List.of(mockTransaction));
        when(repository.findAllByUserId(any(Pageable.class), eq(1L))).thenReturn(page);

        Page<SearchedTransactionDTO> result = service.findAllByUser(1L, 0, 10, "id", "ASC");

        assertEquals(1, result.getTotalElements());
        verify(repository).findAllByUserId(any(Pageable.class), eq(1L));
    }
}
