package com.tech.padawan.financialmanager.user.dto;

import com.tech.padawan.financialmanager.role.model.Role;

import java.util.List;

public record RecoveryUserDTO(
        Long id,
        String email,
        List<Role> roles
) {
}
