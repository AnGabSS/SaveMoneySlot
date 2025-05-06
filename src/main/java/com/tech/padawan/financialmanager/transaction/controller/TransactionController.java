package com.tech.padawan.financialmanager.transaction.controller;

import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.SearchedTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.service.ITransactionService;
import com.tech.padawan.financialmanager.transaction.service.exception.TransactionNotFound;
import com.tech.padawan.financialmanager.user.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private ITransactionService service;

    @GetMapping
    public ResponseEntity<List<SearchedTransactionDTO>> findAll(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "4") int size,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ){
        return ResponseEntity.ok(service.findAll(page, size, orderBy, direction).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable Long id){
        try{
            return ResponseEntity.ok(service.getById(id));
        } catch (TransactionNotFound e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody CreateTransactionDTO transactionDTO){
        try{
            Transaction transaction = service.create(transactionDTO);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(transaction.getId()).toUri();
            return ResponseEntity.created(uri).body(SearchedTransactionDTO.from(transaction));
        } catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody UpdateTransactionDTO transactionDTO){
        try{
            SearchedTransactionDTO newTransation = service.update(id, transactionDTO);
            return ResponseEntity.ok(newTransation);
        } catch (UserNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return ResponseEntity.ok(service.delete(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<SearchedTransactionDTO>> findAllByUser(
            @PathVariable Long id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "4") int size,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ){
        return ResponseEntity.ok(service.findAllByUser(id, page, size, orderBy, direction).getContent());
    }
}

