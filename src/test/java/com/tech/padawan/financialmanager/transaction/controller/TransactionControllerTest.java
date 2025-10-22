package com.tech.padawan.financialmanager.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.padawan.financialmanager.global.config.security.TestSecurityConfig;
import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.party.repository.PartyRepository;
import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import com.tech.padawan.financialmanager.transaction.repository.TransactionCategoryRepository;
import com.tech.padawan.financialmanager.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private TransactionCategoryRepository categoryRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    private Party testParty;
    private TransactionCategory testCategory;

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        partyRepository.deleteAll();

        Party partyToSave = Party.builder()
                .name("Test Party")
                .points(0)
                .balance(BigDecimal.ZERO)
                .champions(new ArrayList<>())
                .build();

        this.testParty = partyRepository.save(partyToSave);
        this.testCategory = categoryRepository.save(new TransactionCategory(null, "Games", TransactionType.EXPENSE, this.testParty));
    }

    @Test
    @DisplayName("Should create a transaction and return 201 Created")
    void shouldCreateATransactionAndReturn201() throws Exception {
        CreateTransactionDTO createDto = new CreateTransactionDTO(testParty.getId(), new BigDecimal("200.00"), "Cyberpunk 2077", testCategory.getId());

        mockMvc.perform(post("/api/v1/transaction")
                        .header("Authorization", "Bearer mock-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.value").value(200.00))
                .andExpect(jsonPath("$.description").value("Cyberpunk 2077"));
    }

    @Test
    @DisplayName("Should return a page of transactions for a Party and return 200 OK")
    void shouldReturnAPageOfTransactionsForParty() throws Exception {
        transactionRepository.save(new Transaction(null, new BigDecimal("250.00"), "Steam Sale", LocalDateTime.now(), testCategory, testParty));
        transactionRepository.save(new Transaction(null, new BigDecimal("70.00"), "DLC Phantom Liberty", LocalDateTime.now(), testCategory, testParty));

        mockMvc.perform(get("/api/v1/transaction/" + testParty.getId() + "?page=1&size=10")
                        .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].description", containsInAnyOrder("Steam Sale", "DLC Phantom Liberty")));
    }

    @Test
    @DisplayName("Should return transaction by ID and return 200 OK")
    void shouldReturnTransactionById() throws Exception {
        Transaction transaction = transactionRepository.save(new Transaction(null, new BigDecimal("150.00"), "Game Pass", LocalDateTime.now(), testCategory, testParty));

        mockMvc.perform(get("/api/v1/transaction/find-by-id/" + transaction.getId())
                        .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transaction.getId()))
                .andExpect(jsonPath("$.description").value("Game Pass"))
                .andExpect(jsonPath("$.value").value(150.00));
    }

    @Test
    @DisplayName("Should update transaction and return 200 OK")
    void shouldUpdateTransaction() throws Exception {
        Transaction transaction = transactionRepository.save(new Transaction(null, new BigDecimal("100.00"), "PSN Credits", LocalDateTime.now(), testCategory, testParty));
        UpdateTransactionDTO updateDto = new UpdateTransactionDTO(new BigDecimal("120.50"), "PSN Credits (new value)", testCategory.getId());

        mockMvc.perform(put("/api/v1/transaction/" + transaction.getId())
                        .header("Authorization", "Bearer mock-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transaction.getId()))
                .andExpect(jsonPath("$.description").value("PSN Credits (new value)"))
                .andExpect(jsonPath("$.value").value(120.50));
    }

    @Test
    @DisplayName("Should delete transaction and return 200 OK")
    void shouldDeleteTransaction() throws Exception {
        Transaction transaction = transactionRepository.save(new Transaction(null, new BigDecimal("50.00"), "Game to be deleted", LocalDateTime.now(), testCategory, testParty));

        mockMvc.perform(delete("/api/v1/transaction/" + transaction.getId())
                        .header("Authorization", "Bearer mock-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction deleted"));
    }
}