package com.tech.padawan.financialmanager.user.service;

import com.tech.padawan.financialmanager.user.dto.*;
import com.tech.padawan.financialmanager.user.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> listAll();
    UserSearchedDTO getById(Long id);
    RecoveryJwtTokenDTO authenticateUser(LoginUserDTO userDTO);
    User create(CreateUserDTO user);
    User update(Long id, UpdateUserDTO user);
    String delete(Long id);
    User updateUserCompleted(User user);
    User getUserEntityById(Long id);
    User addValueToBalance(Long id, BigDecimal value);
    User subtractValueToBalance(Long id, BigDecimal value);

}
