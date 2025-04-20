package com.tech.padawan.financialmanager.user.service;

import com.tech.padawan.financialmanager.user.dto.CreateUserDTO;
import com.tech.padawan.financialmanager.user.dto.LoginUserDTO;
import com.tech.padawan.financialmanager.user.dto.RecoveryJwtTokenDTO;
import com.tech.padawan.financialmanager.user.dto.UserSearchedDTO;
import com.tech.padawan.financialmanager.user.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> listAll();
    UserSearchedDTO getById(Long id);
    RecoveryJwtTokenDTO authenticateUser(LoginUserDTO userDTO);
    User create(CreateUserDTO user);
    User update(Long id,User user);
    String delete(Long id);
}
