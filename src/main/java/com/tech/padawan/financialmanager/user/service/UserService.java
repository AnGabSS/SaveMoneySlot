package com.tech.padawan.financialmanager.user.service;

import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.repository.UserRepository;
import com.tech.padawan.financialmanager.user.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService{

    @Autowired
    private UserRepository repository;

    @Override
    public List<User> listAll() {
        return repository.findAll();
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.of(repository.findById(id)).orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found."));
    }

    @Override
    public User create(User user) {
        user.setPassword(hashCode(user.getPassword()));
        return repository.save(user);
    }

    @Override
    public User update(Long id, User user) {
        return repository.save(user);
    }

    @Override
    public String delete(Long id) {
        repository.deleteById(id);
        return "User deleted";
    }
}
