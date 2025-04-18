package com.tech.padawan.financialmanager.user.controller;

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
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("Hello World");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> findById(@PathVariable Long id){
        return ResponseEntity.ok().body(service.getById(id));
    }
    
    @PostMapping
    public ResponseEntity<User> save(@RequestBody User user){
        User userCreated = service.create(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userCreated.getId()).toUri();

        return ResponseEntity.created(uri).body(user);
    }

}
