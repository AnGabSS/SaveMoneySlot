package com.tech.padawan.financialmanager.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.padawan.financialmanager.global.config.security.JwtTokenService;
import com.tech.padawan.financialmanager.global.config.security.TestSecurityConfig;
import com.tech.padawan.financialmanager.role.model.RoleType;
import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction; // Importe a entidade Transaction
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import com.tech.padawan.financialmanager.transaction.repository.TransactionCategoryRepository;
import com.tech.padawan.financialmanager.transaction.repository.TransactionRepository;
import com.tech.padawan.financialmanager.transaction.service.TransactionCategoryService;
import com.tech.padawan.financialmanager.transaction.service.TransactionService; // Importe o TransactionService
import com.tech.padawan.financialmanager.user.dto.CreateUserDTO;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.repository.UserRepository;
import com.tech.padawan.financialmanager.user.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // Mantido para limpar o contexto entre classes de teste
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionCategoryService categoryService;
    @Autowired
    private TransactionService transactionService; // Serviço para criar transações nos testes
    @Autowired
    private JwtTokenService tokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TransactionCategoryRepository categoryRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    private String jwtToken;
    private Long createdUserId;
    private Long createdCategoryId;

    @BeforeEach
    void setup() {
        // Limpeza explícita garante isolamento total antes de cada teste
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        // ARRANGE (Preparação) Comum: Cria um usuário e uma categoria base para os testes
        User user = userService.create(new CreateUserDTO("Ezio Auditore", "ezio@virenze.com.it", "password123", java.time.LocalDate.now(), RoleType.ADMIN));
        this.createdUserId = user.getId();
        this.jwtToken = tokenService.generateToken(user);

        TransactionCategory category = categoryService.create(this.createdUserId, new CreateTransactionCategoryDTO("Jogos", TransactionType.EXPENSE));
        this.createdCategoryId = category.getId();
    }

    private String bearer() {
        return "Bearer " + this.jwtToken;
    }

    @Test
    @DisplayName("Deve criar uma transação e retornar código 201")
    void shouldCreateATransactionAndReturn201Code() throws Exception {
        // ARRANGE
        CreateTransactionDTO createDto = new CreateTransactionDTO(BigDecimal.valueOf(200.00), "Compra de Cyberpunk 2077", createdCategoryId);

        // ACT & ASSERT
        mockMvc.perform(post("/transaction")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.value").value(200.00))
                .andExpect(jsonPath("$.description").value("Compra de Cyberpunk 2077"));
    }

    @Test
    @DisplayName("Deve retornar uma lista de transações do usuário e código 200")
    void shouldReturnAPageOfTransactionsForUserAndReturn200Code() throws Exception {
        transactionService.create(createdUserId, new CreateTransactionDTO(BigDecimal.valueOf(250.00), "Steam Sale", createdCategoryId));
        transactionService.create(createdUserId, new CreateTransactionDTO(BigDecimal.valueOf(70.00), "DLC Phantom Liberty", createdCategoryId));

        mockMvc.perform(get("/transaction?page=1&size=10")
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].description", containsInAnyOrder("Steam Sale", "DLC Phantom Liberty")));
    }

    @Test
    @DisplayName("Deve retornar uma transação pelo seu ID e código 200")
    void shouldReturnTransactionByIdAndReturn200Code() throws Exception {
        Transaction transaction = transactionService.create(createdUserId, new CreateTransactionDTO(BigDecimal.valueOf(150.00), "Assinatura Game Pass", createdCategoryId));
        Long transactionId = transaction.getId(); // ID dinâmico

        mockMvc.perform(get("/transaction/" + transactionId)
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.description").value("Assinatura Game Pass"))
                .andExpect(jsonPath("$.value").value(150.00));
    }

    @Test
    @DisplayName("Deve atualizar uma transação e retornar código 200")
    void shouldUpdateTransactionAndReturn200Code() throws Exception {
        Transaction transaction = transactionService.create(createdUserId, new CreateTransactionDTO(BigDecimal.valueOf(100.00), "Créditos na PSN", createdCategoryId));
        Long transactionId = transaction.getId(); // ID dinâmico

        UpdateTransactionDTO updateDto = new UpdateTransactionDTO(BigDecimal.valueOf(120.50), "Créditos na PSN (valor corrigido)", createdCategoryId);

        mockMvc.perform(put("/transaction/" + transactionId)
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.description").value("Créditos na PSN (valor corrigido)"))
                .andExpect(jsonPath("$.value").value(120.50));
    }

    @Test
    @DisplayName("Deve deletar uma transação e retornar código 200")
    void shouldDeleteTransactionAndReturn200Code() throws Exception {
        Transaction transaction = transactionService.create(createdUserId, new CreateTransactionDTO(BigDecimal.valueOf(50.00), "Jogo a ser removido", createdCategoryId));
        Long transactionId = transaction.getId(); // ID dinâmico

        mockMvc.perform(delete("/transaction/" + transactionId)
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction deleted"));
    }
}