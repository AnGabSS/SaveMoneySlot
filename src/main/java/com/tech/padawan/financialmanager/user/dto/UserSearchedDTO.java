package com.tech.padawan.financialmanager.user.dto;

import com.tech.padawan.financialmanager.role.model.RoleType;
import com.tech.padawan.financialmanager.user.model.User;

import java.math.BigDecimal;
import java.util.Date;

public record UserSearchedDTO(
        Long id,
        String name,
        String email,
        Date birthdate,
        BigDecimal balance,
        RoleType role
) {
    public static UserSearchedDTO from(User user) {
        return new UserSearchedDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getBirthdate(),
                user.getBalance(),
                user.getLastAddRole()
        );
    }
}
