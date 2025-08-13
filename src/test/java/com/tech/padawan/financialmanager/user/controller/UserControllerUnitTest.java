package com.tech.padawan.financialmanager.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.padawan.financialmanager.global.config.security.JwtTokenService;
import com.tech.padawan.financialmanager.global.config.security.TestSecurityConfig;
import com.tech.padawan.financialmanager.role.model.Role;
import com.tech.padawan.financialmanager.role.model.RoleType;
import com.tech.padawan.financialmanager.user.dto.*;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.service.IUserService;
import com.tech.padawan.financialmanager.user.service.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUserService service;

    @MockitoBean
    private JwtTokenService jwtTokenService;

    @Autowired
    private ObjectMapper objectMapper;


    private static UserSearchedDTO mockUserSearchedDTO;
    private static CreateUserDTO mockCreateUserDTO;
    private static UpdateUserDTO mockUpdateUserDTO;
    private static User mockUser;

    @BeforeAll
    static void setup() throws Exception {
        mockUserSearchedDTO = new UserSearchedDTO(
                1L,
                "David Bowie",
                "david@bowie.com.us",
                LocalDate.parse("1940-04-03"),
                BigDecimal.ZERO,
                RoleType.ADMIN
        );
        mockCreateUserDTO = new CreateUserDTO(
                "David Bowie",
                "david@bowie.com.us",
                "password123",
                LocalDate.parse("1940-04-03"),
                RoleType.ADMIN
        );
        mockUpdateUserDTO = new UpdateUserDTO(
                "Thom Yorke",
                "thom.yorke@radiohead.com.en",
                LocalDate.parse("1968-10-07")
        );
        mockUser = User.builder()
                .id(1L)
                .name("David Bowie")
                .email("david@bowie.com.us")
                .birthdate(LocalDate.parse("1940-04-03"))
                .roles(List.of(Role.builder().name(RoleType.ADMIN).build()))
                .balance(BigDecimal.ZERO)
                .build();
    }

    @Test
    @DisplayName("Should return user when found")
    void shouldReturnUserWhenFound() throws Exception {
        when(service.getById(1L)).thenReturn(mockUserSearchedDTO);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("David Bowie"))
                .andExpect(jsonPath("$.email").value("david@bowie.com.us"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    @DisplayName("Should return 404 code when not found")
    void shouldReturn404WhenUserNotFound() throws Exception {
        when(service.getById(1L)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().is(404))
                .andExpect(content().string("User not found"));
    }

    @Test
    @DisplayName("Should return a page of users and http code 200")
    void shouldReturnAPageOfUserAnd200Code() throws  Exception{
        List<UserSearchedDTO> users = new ArrayList<>();
        users.add(mockUserSearchedDTO);

        Page<UserSearchedDTO> page = new PageImpl<>(users, PageRequest.of(1, 4), 10);

        when(service.listAll(1, 4, "id", "ASC")).thenReturn(page);

        mockMvc.perform(get("/users?page=1&size=4&orderBy=id&direction=ASC"))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(mockUserSearchedDTO.id()))
                .andExpect(jsonPath("$.content[0].name").value(mockUserSearchedDTO.name()))
                .andExpect(jsonPath("$.content[0].email").value(mockUserSearchedDTO.email()))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(4))
                .andExpect(jsonPath("$.totalElements").value(10));
    }

    @Test
    @DisplayName("Should create a user and return http code 201")
    void shouldCreateAUserAndReturnHttpCode201() throws Exception {

        when(service.create(any(CreateUserDTO.class))).thenReturn(mockUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateUserDTO)))
                .andExpect(status().isCreated()) // Aqui vai ser 201 de verdade
                .andExpect(jsonPath("$.name").value("David Bowie"))
                .andExpect(jsonPath("$.email").value("david@bowie.com.us"))
                .andExpect(jsonPath("$.balance").value(0));
    }

    @Test
    @DisplayName("Should update a user and return http code 200")
    void shouldUpdateAUserAndReturnHttpCode200() throws Exception {
        User mockUserUpdated = User.builder()
                .id(1L)
                .name("Thom Yorke")
                .email("thom.yorke@radiohead.com.en")
                .birthdate(LocalDate.parse("1968-10-07"))
                .roles(List.of(Role.builder().name(RoleType.ADMIN).build()))
                .balance(BigDecimal.ZERO)
                .build();

        when(service.update(1L, mockUpdateUserDTO)).thenReturn(mockUserUpdated);
        when(service.getById(1L)).thenReturn(mockUserSearchedDTO);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockUpdateUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Thom Yorke"))
                .andExpect(jsonPath("$.email").value("thom.yorke@radiohead.com.en"))
                .andExpect(jsonPath("$.birthdate").value("1968-10-07"));

    }

    @Test
    @DisplayName("Should delete a user and return http code 200")
    void shouldDeleteAUserAndReturnHttpCode200() throws Exception {
        when(service.delete(1L)).thenReturn("User deleted");

        mockMvc.perform(delete("/users/1")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted"));
    }

    @Test
    @DisplayName("Should authenticate user and return 200 with JWT token")
    void shouldAuthenticateUserAndReturn200WithJwtToken() throws Exception {
        // Arrange
        LoginUserDTO loginRequest = new LoginUserDTO("david@bowie.com.us", "password123");
        RecoveryJwtTokenDTO tokenDTO = new RecoveryJwtTokenDTO("mock-jwt-token");

        when(service.getByEmail(loginRequest.email())).thenReturn(mockUser);

        when(service.authenticateUser(any(LoginUserDTO.class)))
                .thenReturn(tokenDTO);

        // Act & Assert
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    @DisplayName("Should return 400 when email or password is incorrect")
    void shouldReturn400WhenEmailOrPasswordIsIncorrect() throws Exception {
        // Arrange
        LoginUserDTO loginRequest = new LoginUserDTO("wrong@email.com", "wrongpass");

        when(service.authenticateUser(any(LoginUserDTO.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email or password is incorrect"));
    }




}
