package com.tech.padawan.financialmanager.user.controller;

import com.tech.padawan.financialmanager.global.config.security.JwtTokenService;
import com.tech.padawan.financialmanager.global.config.security.TestSecurityConfig;
import com.tech.padawan.financialmanager.role.model.RoleType;
import com.tech.padawan.financialmanager.user.dto.UserSearchedDTO;
import com.tech.padawan.financialmanager.user.service.IUserService;
import com.tech.padawan.financialmanager.user.service.exceptions.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    @DisplayName("Should return user when found")
    void shouldReturnUserWhenFound() throws Exception {
        UserSearchedDTO expectedUser = new UserSearchedDTO(
                1L,
                "David Bowie",
                "david@bowie.com.us",
                new SimpleDateFormat("yyyy-MM-dd").parse("1940-04-03"),
                BigDecimal.ZERO,
                RoleType.ADMIN
        );

        when(service.getById(1L)).thenReturn(expectedUser);

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
}
