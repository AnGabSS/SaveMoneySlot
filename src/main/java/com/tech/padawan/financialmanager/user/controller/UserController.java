package com.tech.padawan.financialmanager.user.controller;

import com.tech.padawan.financialmanager.user.dto.CreateUserDTO;
import com.tech.padawan.financialmanager.user.dto.LoginUserDTO;
import com.tech.padawan.financialmanager.user.dto.RecoveryJwtTokenDTO;
import com.tech.padawan.financialmanager.user.dto.UserSearchedDTO;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping("/{id}")
    public ResponseEntity<UserSearchedDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok().body(service.getById(id));
    }
    
    @PostMapping
    public ResponseEntity<?> save(@RequestBody CreateUserDTO user){
        try{
            User userCreated = service.create(user);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userCreated.getId()).toUri();
            UserSearchedDTO userDTO = UserSearchedDTO.from(userCreated);
            return ResponseEntity.created(uri).body(userDTO);
        } catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<RecoveryJwtTokenDTO> authenticateUser(@RequestBody LoginUserDTO user){
        RecoveryJwtTokenDTO token = service.authenticateUser(user);
        return ResponseEntity.ok(token);
    }

}
