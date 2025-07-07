package com.tech.padawan.financialmanager.user.controller;

import com.tech.padawan.financialmanager.user.dto.*;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.service.IUserService;
import com.tech.padawan.financialmanager.user.service.UserService;
import com.tech.padawan.financialmanager.user.service.exceptions.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
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

    private final IUserService service;

    public UserController(IUserService service){
        this.service = service;
    }

    @Operation(summary = "Get a user by ID", responses = {
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(schema = @Schema(implementation = UserSearchedDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })    @GetMapping("/{id}")
    public ResponseEntity<UserSearchedDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok().body(service.getById(id));
    }
    
    @PostMapping
    public ResponseEntity<UserSearchedDTO> save(@RequestBody @Valid CreateUserDTO user){
        User userCreated = service.create(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userCreated.getId()).toUri();
        UserSearchedDTO userDTO = UserSearchedDTO.from(userCreated);
        return ResponseEntity.created(uri).body(userDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> authenticateUser(@RequestBody LoginUserDTO user){
        try{
            RecoveryJwtTokenDTO token = service.authenticateUser(user);
            return ResponseEntity.ok(token);
        } catch (BadCredentialsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password is incorrect");
        } catch (InternalAuthenticationServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserSearchedDTO> update(@PathVariable Long id, @RequestBody UpdateUserDTO user){
        User userCreated = service.update(id, user);
        UserSearchedDTO userDTO = UserSearchedDTO.from(userCreated);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        return ResponseEntity.ok(service.delete(id));
    }

}
