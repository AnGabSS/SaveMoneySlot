package com.tech.padawan.financialmanager.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.padawan.financialmanager.global.config.security.TestSecurityConfig;
import com.tech.padawan.financialmanager.user.dto.*;
import com.tech.padawan.financialmanager.role.model.RoleType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import jakarta.transaction.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static Long createdUserId;
    private static String jwtToken;


    private String bearer() {
        return "Bearer " + jwtToken;
    }

    @Test
    @Order(1)
    @DisplayName("Create user and return 201")
    void shouldCreateUser() throws Exception {
        CreateUserDTO createDto = new CreateUserDTO(
                "David Bowie",
                "david@bowie.com.us",
                "password123",
                java.time.LocalDate.parse("1940-04-03"),
                RoleType.ADMIN
        );

        MvcResult result = mockMvc.perform(post("/users")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("David Bowie"))
                .andExpect(jsonPath("$.email").value("david@bowie.com.us"))
                .andExpect(jsonPath("$.balance").value(0))
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        UserSearchedDTO userCreated = objectMapper.readValue(responseJson, UserSearchedDTO.class);
        createdUserId = userCreated.id();
    }

    @Test
    @Order(2)
    @DisplayName("Authenticate user with valid credentials and return JWT token")
    void shouldAuthenticateUserAndReturnToken() throws Exception {

        LoginUserDTO loginDto = new LoginUserDTO("david@bowie.com.us", "password123");

        MvcResult result = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        RecoveryJwtTokenDTO tokenResponse = objectMapper.readValue(responseJson, RecoveryJwtTokenDTO.class);
        jwtToken = tokenResponse.token();
    }

    @Test
    @Order(3)
    @DisplayName("List users and verify created user is present")
    void shouldListUsersAndContainCreatedUser() throws Exception {
        mockMvc.perform(get("/users?page=1&size=10&orderBy=id&direction=ASC")
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[*].id", hasItem(createdUserId.intValue())))
                .andExpect(jsonPath("$.content[*].name", hasItem("David Bowie")));
    }

    @Test
    @Order(4)
    @DisplayName("Get user by ID")
    void shouldGetUserById() throws Exception {
        mockMvc.perform(get("/users/" + createdUserId)
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUserId))
                .andExpect(jsonPath("$.name").value("David Bowie"))
                .andExpect(jsonPath("$.email").value("david@bowie.com.us"));
    }

    @Test
    @Order(5)
    @DisplayName("Update user")
    void shouldUpdateUser() throws Exception {
        UpdateUserDTO updateDto = new UpdateUserDTO(
                "Thom Yorke",
                "thom.yorke@radiohead.com.en",
                java.time.LocalDate.parse("1968-10-07")
        );

        mockMvc.perform(put("/users/" + createdUserId)
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Thom Yorke"))
                .andExpect(jsonPath("$.email").value("thom.yorke@radiohead.com.en"))
                .andExpect(jsonPath("$.birthdate").value("1968-10-07"));
    }

    @Test
    @Order(5)
    @DisplayName("Delete user")
    void shouldDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/" + createdUserId)
                        .header("Authorization", bearer()))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted"));

        mockMvc.perform(get("/users/" + createdUserId)
                        .header("Authorization", bearer()))
                .andExpect(status().isNotFound());
    }
}
