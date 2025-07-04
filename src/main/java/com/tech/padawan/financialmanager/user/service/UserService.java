package com.tech.padawan.financialmanager.user.service;

import com.tech.padawan.financialmanager.global.config.security.JwtTokenService;
import com.tech.padawan.financialmanager.role.model.Role;
import com.tech.padawan.financialmanager.user.dto.*;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.repository.UserRepository;
import com.tech.padawan.financialmanager.user.service.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService{

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> listAll() {
        return repository.findAll();
    }

    @Override
    public UserSearchedDTO getById(Long id) {
        User user = Optional.of(repository.findById(id)).get().orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found."));
        return UserSearchedDTO.from(user);
    }

    @Override
    public RecoveryJwtTokenDTO authenticateUser(LoginUserDTO loginUserDto) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginUserDto.email(), loginUserDto.password());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        String email = authentication.getName();

        User user = repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new RecoveryJwtTokenDTO(jwtTokenService.generateToken(user));
    }

        @Override
    public User create(CreateUserDTO userDTO) {
        User user = User.builder()
                .name(userDTO.name())
                .email(userDTO.email())
                .password(passwordEncoder.encode(userDTO.password()))
                .birthdate(userDTO.birthdate())
                .roles(List.of(Role.builder().name(userDTO.role()).build()))
                .balance(BigDecimal.ZERO)
                .build();
        return repository.save(user);
    }

    @Override
    public User update(Long id, UpdateUserDTO user) {
        User oldUser = repository.getReferenceById(id);
        oldUser.setName(user.name());
        oldUser.setEmail(user.email());
        oldUser.setBirthdate(user.birthdate());
        return repository.save(oldUser);
    }

    @Override
    public String delete(Long id) {
        this.getById(id);
        repository.deleteById(id);
        return "User deleted";
    }

    @Override
    public User updateUserCompleted(User user) {
        return repository.save(user);
    }


    @Override
    public User getUserEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found."));
    }

    @Override
    public User addValueToBalance(Long id, BigDecimal value) {
        User oldUser = repository.getReferenceById(id);
        BigDecimal newValue = oldUser.getBalance().add(value);
        oldUser.setBalance(newValue);
        return repository.save(oldUser);
    }

    @Override
    public User subtractValueToBalance(Long id, BigDecimal value) {
        User oldUser = repository.getReferenceById(id);
        BigDecimal newValue = oldUser.getBalance().subtract(value);
        oldUser.setBalance(newValue);
        return repository.save(oldUser);
    }

}
