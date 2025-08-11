package com.tech.padawan.financialmanager.user.service;

import com.tech.padawan.financialmanager.user.dto.*;
import com.tech.padawan.financialmanager.user.model.User;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IUserService {
    Page<UserSearchedDTO> listAll(Integer page, Integer size, String orderBy, String direction);
    UserSearchedDTO getById(Long id);
    User getByEmail(String email);
    RecoveryJwtTokenDTO authenticateUser(LoginUserDTO userDTO);
    User create(CreateUserDTO user);
    User update(Long id, UpdateUserDTO user);
    String delete(Long id);
    User updateUserCompleted(User user);
    User getUserEntityById(Long id);
    User addValueToBalance(Long id, BigDecimal value);
    User subtractValueToBalance(Long id, BigDecimal value);

}
