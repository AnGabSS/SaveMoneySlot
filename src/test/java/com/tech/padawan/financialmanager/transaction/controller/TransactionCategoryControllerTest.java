package com.tech.padawan.financialmanager.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.padawan.financialmanager.global.config.security.TestSecurityConfig;
import com.tech.padawan.financialmanager.role.model.RoleType;
import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import com.tech.padawan.financialmanager.transaction.service.TransactionCategoryService;
import com.tech.padawan.financialmanager.user.dto.CreateUserDTO;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionCategoryControllerTest {

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
                "Altair Ibn-La’Ahad",
                "altair@assassins.com",
                "creed123",
                java.time.LocalDate.parse("1935-07-11"),
                RoleType.ADMIN
        );

        User userCreated = userService.create(createDto);
        createdUserId = userCreated.getId();

        CreateTransactionCategoryDTO createCategoryDTO = new CreateTransactionCategoryDTO(
                "Entertainment",
                TransactionType.EXPENSE,
                createdUserId
        );

        TransactionCategory categoryCreated = categoryService.create(createCategoryDTO);
        createdCategoryId = categoryCreated.getId();
    }

    @Test
    @Order(1)
    @DisplayName("Should create a category and return 201 code")
    void shouldCreateACategoryAndReturn201Code() throws Exception {
        CreateTransactionCategoryDTO createDTO = new CreateTransactionCategoryDTO(
                "Food",
                TransactionType.EXPENSE,
                createdUserId
        );

        mockMvc.perform(post("/transaction/category")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Food"))
                .andExpect(jsonPath("$.type").value("EXPENSE"))
                .andReturn();
    }

    @Test
    @Order(2)
    @DisplayName("Should return a page of categories list and 200 code")
    void shouldReturnAPageOfCategoriesListAnd200Code() throws Exception {
        mockMvc.perform(get("/transaction/category?page=0&size=10&orderBy=id&direction=ASC")
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].id", hasItem(createdCategoryId.intValue())))
                .andExpect(jsonPath("$[*].name", hasItem("Entertainment")));
    }

    @Test
    @Order(3)
    @DisplayName("Should return a category by the ID and 200 code")
    void shouldReturnACategoryByTheIDAnd200Code() throws Exception {
        mockMvc.perform(get("/transaction/category/" + createdCategoryId)
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdCategoryId))
                .andExpect(jsonPath("$.name").value("Entertainment"))
                .andExpect(jsonPath("$.type").value("EXPENSE"));
    }

    @Test
    @Order(4)
    @DisplayName("Should return a page of categories by user and 200 code")
    void shouldReturnACategoriesByUserAnd200Code() throws Exception {
        mockMvc.perform(get("/transaction/category/user/" + createdUserId + "?page=0&size=10&orderBy=id&direction=ASC")
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].id", hasItem(createdCategoryId.intValue())))
                .andExpect(jsonPath("$[*].name", hasItem("Entertainment")));
    }

    @Test
    @Order(5)
    @DisplayName("Should update a category and return 200 code")
    void shouldUpdateACategoryAndReturn200Code() throws Exception {
        UpdateTransactionCategoryDTO updateDTO = new UpdateTransactionCategoryDTO(
                "Entertainment Updated",
                TransactionType.EXPENSE
        );

        mockMvc.perform(put("/transaction/category/" + createdCategoryId)
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdCategoryId))
                .andExpect(jsonPath("$.name").value("Entertainment Updated"));
    }

    @Test
    @Order(6)
    @DisplayName("Should delete a category and return 200 code")
    void shouldDeleteACategoryAndReturn200Code() throws Exception {
        mockMvc.perform(delete("/transaction/category/" + createdCategoryId)
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction category deleted"));
    }
}
