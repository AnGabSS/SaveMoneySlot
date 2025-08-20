package com.tech.padawan.financialmanager.global.config.security;

import com.tech.padawan.financialmanager.role.model.Role;
import com.tech.padawan.financialmanager.role.model.RoleType;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserAuthenticationFilterTest {

    @Mock
    private JwtTokenService jwtTokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private UserAuthenticationFilter filter;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        filter = new UserAuthenticationFilter(jwtTokenService, userRepository);
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Should skip OPTIONS requests")
    void shouldSkipOptionsRequests() throws Exception {
        when(request.getMethod()).thenReturn("OPTIONS");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("Should allow public endpoints without authentication")
    void shouldAllowPublicEndpoints() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getServletPath()).thenReturn("/users/login"); // ajuste para um endpoint que esteja em SecurityConfiguration.PUBLIC_ENDPOINTS

        // proteção extra: caso caia erroneamente no bloco de erro
        StringWriter responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }


    @Test
    @DisplayName("Should authenticate user with valid token")
    void shouldAuthenticateUserWithValidToken() throws Exception {
        String token = "valid.jwt.token";
        String email = "altair@assassins.com";

        when(request.getMethod()).thenReturn("GET");
        when(request.getServletPath()).thenReturn("/transaction/category");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenService.getSubjectFromToken(token)).thenReturn(email);

        User user = User.builder()
                .name("Altair")
                .email(email)
                .password("password")
                .birthdate(LocalDate.parse("1950-12-12"))
                .roles(List.of(new Role(1L, RoleType.ADMIN)))
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        filter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(email, authentication.getPrincipal());

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should return 401 when token is missing")
    void shouldReturn401WhenTokenIsMissing() throws Exception {
        when(request.getMethod()).thenReturn("GET");
        when(request.getServletPath()).thenReturn("/transaction/category");
        when(request.getHeader("Authorization")).thenReturn(null);

        StringWriter responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        filter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseWriter.toString().contains("Missing token"));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Should return 401 when user not found")
    void shouldReturn401WhenUserNotFound() throws Exception {
        String token = "valid.jwt.token";
        String email = "unknown@assassins.com";

        when(request.getMethod()).thenReturn("GET");
        when(request.getServletPath()).thenReturn("/transaction/category");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenService.getSubjectFromToken(token)).thenReturn(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        StringWriter responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        filter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseWriter.toString().contains("User not found"));
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    @DisplayName("Should return 401 when token parsing fails")
    void shouldReturn401WhenTokenParsingFails() throws Exception {
        String token = "invalid.jwt.token";

        when(request.getMethod()).thenReturn("GET");
        when(request.getServletPath()).thenReturn("/transaction/category");
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenService.getSubjectFromToken(token)).thenThrow(new RuntimeException("Invalid token"));

        StringWriter responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));

        filter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(responseWriter.toString().contains("Invalid token"));
        verify(filterChain, never()).doFilter(request, response);
    }
}
