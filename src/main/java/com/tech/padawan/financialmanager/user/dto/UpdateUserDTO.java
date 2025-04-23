package com.tech.padawan.financialmanager.user.dto;

import com.tech.padawan.financialmanager.role.model.RoleType;

import java.util.Date;

public record UpdateUserDTO (
        String name,
        String email,
        Date birthdate
){
}
