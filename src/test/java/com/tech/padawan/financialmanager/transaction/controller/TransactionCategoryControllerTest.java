package com.tech.padawan.financialmanager.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.padawan.financialmanager.global.config.security.JwtTokenService;
import com.tech.padawan.financialmanager.global.config.security.TestSecurityConfig;
import com.tech.padawan.financialmanager.role.model.RoleType;
import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.model.TransactionType;
import com.tech.padawan.financialmanager.transaction.repository.TransactionCategoryRepository;
import com.tech.padawan.financialmanager.transaction.repository.TransactionRepository;
import com.tech.padawan.financialmanager.transaction.service.TransactionCategoryService;
import com.tech.padawan.financialmanager.user.dto.CreateUserDTO;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.repository.UserRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
// Esta anotação ainda é muito útil para garantir que o BD esteja limpo para a PRÓXIMA classe de teste
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TransactionCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenService tokenService;

    @Autowired
    private TransactionCategoryService categoryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionCategoryRepository categoryRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    private String jwtToken;
    private Long createdUserId;

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        CreateUserDTO createDto = new CreateUserDTO(
                "Altair Ibn-La’Ahad",
                "altair@assassins.com",
                "creed123",
                java.time.LocalDate.parse("1935-07-11"),
                RoleType.ADMIN
        );

        User userCreated = userService.create(createDto);
        this.createdUserId = userCreated.getId();
        this.jwtToken = tokenService.generateToken(userCreated);
    }

    private String bearer() {
        return "Bearer " + this.jwtToken;
    }

    @Test
    @DisplayName("Deve criar uma categoria e retornar código 201")
    void shouldCreateACategoryAndReturn201Code() throws Exception {
        CreateTransactionCategoryDTO createDTO = new CreateTransactionCategoryDTO("Alimentação", TransactionType.EXPENSE);

        mockMvc.perform(post("/transaction/category")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alimentação"))
                .andExpect(jsonPath("$.type").value("EXPENSE"));
    }

    @Test
    @DisplayName("Deve retornar uma página de categorias para o usuário")
    void shouldReturnAPageOfCategoriesList() throws Exception {
        // ARRANGE (Preparação): Crie os dados específicos para este teste
        categoryService.create(createdUserId, new CreateTransactionCategoryDTO("Lazer", TransactionType.EXPENSE));
        categoryService.create(createdUserId, new CreateTransactionCategoryDTO("Salário", TransactionType.INCOME));

        mockMvc.perform(get("/transaction/category?page=1&size=10")
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[*].name", containsInAnyOrder("Lazer", "Salário")));
    }

    @Test
    @DisplayName("Deve retornar uma categoria pelo seu ID")
    void shouldReturnACategoryByTheID() throws Exception {
        TransactionCategory category = categoryService.create(createdUserId, new CreateTransactionCategoryDTO("Saúde", TransactionType.EXPENSE));

        mockMvc.perform(get("/transaction/category/" + category.getId())
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value("Saúde"));
    }

    @Test
    @DisplayName("Deve atualizar uma categoria e retornar código 200")
    void shouldUpdateACategoryAndReturn200Code() throws Exception {
        TransactionCategory originalCategory = categoryService.create(createdUserId, new CreateTransactionCategoryDTO("Lazer", TransactionType.EXPENSE));

        UpdateTransactionCategoryDTO updateDTO = new UpdateTransactionCategoryDTO("Lazer Atualizado", TransactionType.EXPENSE);

        mockMvc.perform(put("/transaction/category/" + originalCategory.getId())
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(originalCategory.getId()))
                .andExpect(jsonPath("$.name").value("Lazer Atualizado"));
    }

    @Test
    @DisplayName("Deve deletar uma categoria e retornar código 200")
    void shouldDeleteACategoryAndReturn200Code() throws Exception {
        TransactionCategory categoryToDelete = categoryService.create(createdUserId, new CreateTransactionCategoryDTO("Temporário", TransactionType.EXPENSE));

        mockMvc.perform(delete("/transaction/category/" + categoryToDelete.getId())
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction category deleted"));
    }
}
