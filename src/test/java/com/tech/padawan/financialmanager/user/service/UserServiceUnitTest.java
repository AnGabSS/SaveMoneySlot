package com.tech.padawan.financialmanager.user.service;

import com.tech.padawan.financialmanager.role.model.Role;
import com.tech.padawan.financialmanager.role.model.RoleType;
import com.tech.padawan.financialmanager.user.dto.CreateUserDTO;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserServiceUnitTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should create a user when everything is ok")
    void create() throws ParseException {
        // Arrange
        CreateUserDTO userDTO = new CreateUserDTO(
                "David Bowie",
                "david@bowie.com.us",
                "david123456",
                LocalDate.parse("1940-04-03"),
                RoleType.ADMIN
        );

        String encodedPassword = "encodedPassword123";
        when(passwordEncoder.encode("david123456")).thenReturn(encodedPassword);

        User expectedUser = User.builder()
                .name("David Bowie")
                .email("david@bowie.com.us")
                .password(encodedPassword)
                .birthdate(userDTO.birthdate())
                .roles(List.of(Role.builder().name(RoleType.ADMIN).build()))
                .balance(BigDecimal.ZERO)
                .build();

        when(repository.save(any(User.class))).thenReturn(expectedUser);

        // Act
        User createdUser = service.create(userDTO);

        // Assert
        assertEquals("David Bowie", createdUser.getName());
        assertEquals("david@bowie.com.us", createdUser.getEmail());
        assertEquals(encodedPassword, createdUser.getPassword());
        assertEquals(BigDecimal.ZERO, createdUser.getBalance());
        assertEquals(1, createdUser.getRoles().size());
        assertEquals(RoleType.ADMIN, createdUser.getRoles().get(0).getName());

        verify(repository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("david123456");
    }

}
