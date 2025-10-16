package com.tech.padawan.financialmanager.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.padawan.financialmanager.global.config.security.TestSecurityConfig;
import com.tech.padawan.financialmanager.user.dto.*;
import com.tech.padawan.financialmanager.role.model.RoleType;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private static Long createdUserId;
    private static String jwtToken;


    private String bearer() {
        return "Bearer " + jwtToken;
    }

    @BeforeEach
    void setup() throws Exception {
        userRepository.deleteAll();
        CreateUserDTO createDto = new CreateUserDTO(
                "David Bowie",
                "david@bowie.com.us",
                "password123",
                java.time.LocalDate.parse("1940-04-03"),
                RoleType.ADMIN
        );
        userService.create(createDto);
    }

    @AfterEach
    void after() throws Exception{
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Authenticate user with valid credentials and return JWT token")
    void shouldAuthenticateUserAndReturnToken() throws Exception {

        LoginUserDTO loginDto = new LoginUserDTO("david@bowie.com.us", "password123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();
        RecoveryJwtTokenDTO tokenResponse = objectMapper.readValue(responseJson, RecoveryJwtTokenDTO.class);
        jwtToken = tokenResponse.token();
    }

}
