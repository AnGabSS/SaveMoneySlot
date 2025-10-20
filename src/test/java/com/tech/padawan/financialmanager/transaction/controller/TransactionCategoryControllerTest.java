package com.tech.padawan.financialmanager.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.padawan.financialmanager.party.model.Party;
import com.tech.padawan.financialmanager.party.repository.PartyRepository;
import com.tech.padawan.financialmanager.global.config.security.TestSecurityConfig;
import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionCategoryDTO;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TransactionCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Repositórios para setup de dados
    @Autowired
    private PartyRepository partyRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private TransactionCategoryRepository categoryRepository;

    private Party testParty;

    @BeforeEach
    void setup() {
        // Limpa os repositórios na ordem correta para evitar conflitos de FK
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        partyRepository.deleteAll();

        // Cria uma Party para ser usada nos testes, em vez de um User
        Party party = new Party();
        party.setName("Test Party");
        this.testParty = partyRepository.save(party);
    }

    @Test
    @DisplayName("Should create a category and return 201 Created")
    void shouldCreateACategoryAndReturn201() throws Exception {
        // DTO agora precisa do partyId
        CreateTransactionCategoryDTO createDTO = new CreateTransactionCategoryDTO(1L, "Food", TransactionType.EXPENSE);

        mockMvc.perform(post("/api/v1/transaction/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Food"))
                .andExpect(jsonPath("$.type").value("EXPENSE"));
    }

    @Test
    @DisplayName("Should return a page of categories for a specific Party")
    void shouldReturnAPageOfCategoriesByParty() throws Exception {
        // Cria categorias associadas à nossa Party de teste
        categoryRepository.save(new TransactionCategory(null, "Leisure", TransactionType.EXPENSE, testParty));
        categoryRepository.save(new TransactionCategory(null, "Salary", TransactionType.INCOME, testParty));

        // Endpoint agora busca por partyId
        mockMvc.perform(get("/api/v1/transaction/category/find-by-id/" + testParty.getId() + "?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("Leisure", "Salary")));
    }

    @Test
    @DisplayName("Should return a category by its ID")
    void shouldReturnACategoryByItsID() throws Exception {
        TransactionCategory category = categoryRepository.save(new TransactionCategory(null, "Health", TransactionType.EXPENSE, testParty));

        mockMvc.perform(get("/api/v1/transaction/category/" + category.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value("Health"));
    }

    @Test
    @DisplayName("Should update a category and return 200 OK")
    void shouldUpdateACategoryAndReturn200() throws Exception {
        TransactionCategory originalCategory = categoryRepository.save(new TransactionCategory(null, "Leisure", TransactionType.EXPENSE, testParty));
        UpdateTransactionCategoryDTO updateDTO = new UpdateTransactionCategoryDTO("Updated Leisure", TransactionType.EXPENSE);

        mockMvc.perform(put("/api/v1/transaction/category/" + originalCategory.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(originalCategory.getId()))
                .andExpect(jsonPath("$.name").value("Updated Leisure"));
    }

    @Test
    @DisplayName("Should delete a category and return 200 OK")
    void shouldDeleteACategoryAndReturn200() throws Exception {
        TransactionCategory categoryToDelete = categoryRepository.save(new TransactionCategory(null, "Temporary", TransactionType.EXPENSE, testParty));

        mockMvc.perform(delete("/api/v1/transaction/category/" + categoryToDelete.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction category deleted"));
    }
}