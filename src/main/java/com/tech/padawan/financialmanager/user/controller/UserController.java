package com.tech.padawan.financialmanager.user.controller;

import com.tech.padawan.financialmanager.user.dto.*;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.service.IUserService;
import com.tech.padawan.financialmanager.user.service.UserService;
import com.tech.padawan.financialmanager.user.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserService service;

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
    public ResponseEntity<Object> authenticateUser(@RequestBody LoginUserDTO user){
        try{
            RecoveryJwtTokenDTO token = service.authenticateUser(user);
            return ResponseEntity.ok(token);
        } catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password is incorrect");
        } catch (InternalAuthenticationServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Not Found");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody UpdateUserDTO user){
        try{
            User userCreated = service.update(id, user);
            UserSearchedDTO userDTO = UserSearchedDTO.from(userCreated);
            return ResponseEntity.ok(userDTO);
        }catch (UserNotFoundException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        return ResponseEntity.ok(service.delete(id));
    }

}
