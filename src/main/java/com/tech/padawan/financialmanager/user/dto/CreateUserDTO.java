package com.tech.padawan.financialmanager.user.dto;

import com.tech.padawan.financialmanager.role.model.Role;
import com.tech.padawan.financialmanager.role.model.RoleType;

import java.util.Date;

public record CreateUserDTO(
        String name,
        String email,
        String password,
        Date birthdate,
        RoleType role
) {
}
