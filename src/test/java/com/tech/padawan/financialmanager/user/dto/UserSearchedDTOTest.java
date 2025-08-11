package com.tech.padawan.financialmanager.user.dto;

import com.tech.padawan.financialmanager.role.model.Role;
import com.tech.padawan.financialmanager.role.model.RoleType;
import com.tech.padawan.financialmanager.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserSearchedDTOTest {

    @Test
    @DisplayName("Should return the currect fields")
    void from() throws ParseException {
        LocalDate birthdate = LocalDate.parse("2004-09-12");

        Role role = Role.builder()
                .id(1L)
                .name(RoleType.ADMIN)
                .build();
        List<Role> roleList = Arrays.asList(role);
        User user = User.builder()
                .id(1L)
                .name("David Bowie")
                .email("david@bowie.com")
                .password("1234")
                .roles(roleList)
                .balance(BigDecimal.ZERO)
                .birthdate(birthdate)
                .build();

        UserSearchedDTO dto = new UserSearchedDTO(1L, "David Bowie", "david@bowie.com", birthdate, BigDecimal.ZERO, RoleType.ADMIN);

        UserSearchedDTO dtoConvertedByFrom = UserSearchedDTO.from(user);

        assertEquals(dtoConvertedByFrom, dto);
    }
}