package com.tech.padawan.financialmanager.user.service;

import com.tech.padawan.financialmanager.config.JwtTokenService;
import com.tech.padawan.financialmanager.config.SecurityConfiguration;
import com.tech.padawan.financialmanager.role.model.Role;
import com.tech.padawan.financialmanager.user.dto.CreateUserDTO;
import com.tech.padawan.financialmanager.user.dto.LoginUserDTO;
import com.tech.padawan.financialmanager.user.dto.RecoveryJwtTokenDTO;
import com.tech.padawan.financialmanager.user.dto.UserSearchedDTO;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.repository.UserRepository;
import com.tech.padawan.financialmanager.user.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserRepository repository;

    @Autowired
    private SecurityConfiguration securityConfiguration;

    @Override
    public List<User> listAll() {
        return repository.findAll();
    }

    @Override
    public UserSearchedDTO getById(Long id) {
        User user = Optional.of(repository.findById(id)).get().orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found."));
        UserSearchedDTO userDTO = UserSearchedDTO.from(user);
        return userDTO;
    }

    @Override
    public RecoveryJwtTokenDTO authenticateUser(LoginUserDTO loginUserDto) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginUserDto.email(), loginUserDto.password());

        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        User user = (User) authentication.getPrincipal();
        return new RecoveryJwtTokenDTO(jwtTokenService.generateToken(user));

    }

        @Override
    public User create(CreateUserDTO userDTO) {
        User user = User.builder()
                .name(userDTO.name())
                .email(userDTO.email())
                .password(securityConfiguration.passwordEncoder().encode(userDTO.password()))
                .birthdate(userDTO.birthdate())
                .roles(List.of(Role.builder().name(userDTO.role()).build()))
                .balance(0)
                .build();
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
