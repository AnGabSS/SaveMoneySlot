package com.tech.padawan.financialmanager.user.service;

import com.tech.padawan.financialmanager.user.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> listAll();
    Optional<User> getById(Long id);
    User create(User user);
    User update(Long id,User user);
    String delete(Long id);
}
