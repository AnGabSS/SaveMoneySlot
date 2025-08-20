package com.tech.padawan.financialmanager.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.padawan.financialmanager.global.config.security.TestSecurityConfig;
import com.tech.padawan.financialmanager.role.model.RoleType;
import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import com.tech.padawan.financialmanager.transaction.service.TransactionCategoryService;
import com.tech.padawan.financialmanager.user.dto.CreateUserDTO;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long createdUserId;
    private Long createdCategoryId;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionCategoryService categoryService;
    private static String jwtToken;


    private String bearer() {
        return "Bearer " + jwtToken;
    }

    @BeforeAll
    void setup() {
        CreateUserDTO createDto = new CreateUserDTO(
                "Ezio Auditore da Virenze",
                "ezio@virenze.com.it",
                "password123",
                java.time.LocalDate.parse("1940-04-03"),
                RoleType.ADMIN
        );

        User userCreated = userService.create(createDto);
        createdUserId = userCreated.getId();

        CreateTransactionCategoryDTO createCategoryDTO = new CreateTransactionCategoryDTO(
                "Games",
                TransactionType.EXPENSE,
                createdUserId
        );

        TransactionCategory categoryCreated = categoryService.create(createCategoryDTO);
        createdCategoryId = categoryCreated.getId();
    }

    @Test
    @DisplayName("Shoud create a transaction and return 201 code")
    @Order(1)
    void shouldCreateATransactionAnd201Code() throws Exception {
        CreateTransactionDTO createTransactionDTO = new CreateTransactionDTO(
                BigDecimal.valueOf(200.0),
                "Game pass",
                createdUserId,
                createdCategoryId
        );

        mockMvc.perform(post("/transaction")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createTransactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.value").value(BigDecimal.valueOf(200.0)))
                .andExpect(jsonPath("$.description").value("Game pass"))
                .andReturn();
    }

    @Test
    @Order(2)
    @DisplayName("Should return a page of transaction list and 200 code")
    void shouldReturnAPageOfTransactionListAnd200Code() throws Exception{
        mockMvc.perform(get("/transaction?page=1&size=10&orderBy=id&direction=ASC")
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[*].id", hasItem(1)))
                .andExpect(jsonPath("$.content[*].description", hasItem("Game pass")));

    }

    @Test
    @Order(3)
    @DisplayName("Should return a transaction by the ID and 200 code")
    void shouldReturnATransactionByTheIDAnd200Code() throws Exception {
        mockMvc.perform(get("/transaction/1")
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Game pass"))
                .andExpect(jsonPath("$.value").value(200.0));
    }


    @Test
    @Order(4)
    @DisplayName("Should return a page of transaction list by user and 200 code")
    void shouldReturnAPageOfTransactionListByUserAnd200Code() throws Exception {
        mockMvc.perform(get("/transaction/user/1?page=1&size=10&orderBy=id&direction=ASC")
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[*].id", hasItem(1)))
                .andExpect(jsonPath("$.content[*].description", hasItem("Game pass")))
                .andExpect(jsonPath("$.content[*].value", hasItem(200.0)));

    }

    @Test
    @Order(5)
    @DisplayName("Should update a transaction and return 200 code")
    void shouldUpdateATransactionAndReturn200Code() throws Exception{
        UpdateTransactionDTO updateDTO = new UpdateTransactionDTO(
                BigDecimal.valueOf(300.0),
                "Game pass ultimate",
                createdCategoryId
        );

        mockMvc.perform(put("/transaction/1")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Game pass ultimate"))
                .andExpect(jsonPath("$.value").value((300.0)));

    }

    @Test
    @Order(6)
    @DisplayName("Should delete a transaction and return 200 code")
    void ShouldDeleteATransactionAndReturn200code() throws Exception{
        mockMvc.perform(delete("/transaction/1")
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction deleted"));
    }

}