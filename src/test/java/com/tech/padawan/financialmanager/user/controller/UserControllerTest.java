package com.tech.padawan.financialmanager.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tech.padawan.financialmanager.global.config.security.JwtTokenService;
import com.tech.padawan.financialmanager.global.config.security.TestSecurityConfig;
import com.tech.padawan.financialmanager.role.model.Role;
import com.tech.padawan.financialmanager.role.model.RoleType;
import com.tech.padawan.financialmanager.user.dto.CreateUserDTO;
import com.tech.padawan.financialmanager.user.dto.UserSearchedDTO;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.service.IUserService;
import com.tech.padawan.financialmanager.user.service.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IUserService service;

    @MockitoBean
    private JwtTokenService jwtTokenService;

    @MockitoBean
    private UserController controller;

    @Autowired
    private ObjectMapper objectMapper;


    private static UserSearchedDTO mockUserSearchedDTO;
    private static CreateUserDTO mockCreateUserDTO;

    @BeforeAll
    static void setup() throws Exception {
        mockUserSearchedDTO = new UserSearchedDTO(
                1L,
                "David Bowie",
                "david@bowie.com.us",
                new SimpleDateFormat("yyyy-MM-dd").parse("1940-04-03"),
                BigDecimal.ZERO,
                RoleType.ADMIN
        );
        mockCreateUserDTO = new CreateUserDTO(
                "David Bowie",
                "david@bowie.com.us",
                "password123",
                new SimpleDateFormat("yyyy-MM-dd").parse("1940-04-03"),
                RoleType.ADMIN
        );
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
        // Mock do User retornado pelo service
        User mockUser = User.builder()
                .id(1L)
                .name("David Bowie")
                .email("david@bowie.com.us")
                .birthdate(new SimpleDateFormat("yyyy-MM-dd").parse("1940-04-03"))
                .roles(List.of(Role.builder().name(RoleType.ADMIN).build()))
                .balance(BigDecimal.ZERO)
                .build();

        when(service.create(any(CreateUserDTO.class))).thenReturn(mockUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCreateUserDTO)))
                .andExpect(status().isCreated()) // Aqui vai ser 201 de verdade
                .andExpect(jsonPath("$.name").value("David Bowie"))
                .andExpect(jsonPath("$.email").value("david@bowie.com.us"))
                .andExpect(jsonPath("$.balance").value(0));
    }


}
