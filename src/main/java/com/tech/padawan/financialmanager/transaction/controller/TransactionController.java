package com.tech.padawan.financialmanager.transaction.controller;

import com.tech.padawan.financialmanager.global.config.security.JwtTokenService;
import com.tech.padawan.financialmanager.global.exception.NotFoundException;
import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.SearchedTransactionDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionDTO;
import com.tech.padawan.financialmanager.transaction.model.Transaction;
import com.tech.padawan.financialmanager.transaction.repository.TransactionRepository;
import com.tech.padawan.financialmanager.transaction.service.ITransactionBalanceService;
import com.tech.padawan.financialmanager.transaction.service.ITransactionCategoryService;
import com.tech.padawan.financialmanager.transaction.service.ITransactionService;
import com.tech.padawan.financialmanager.transaction.service.TransactionService;
import com.tech.padawan.financialmanager.transaction.service.exception.TransactionNotFound;
import com.tech.padawan.financialmanager.user.service.IUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/transaction")
@Tag(
        name = "Transactions",
        description = "Transactions carried out by the user, such as buying a game or receiving their salary."
)
public class TransactionController {

    private final ITransactionService service;

    private final JwtTokenService tokenService;

    public TransactionController(ITransactionService service, JwtTokenService tokenService) {
        this.service = service;
        this.tokenService = tokenService;
    }

    @GetMapping
    public ResponseEntity<Page<SearchedTransactionDTO>> findAll(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "4") int size,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ){
        String jwtToken = authorizationHeader.substring(7);
        String email = tokenService.getSubjectFromToken(jwtToken);
        return ResponseEntity.ok(service.findAllByUserEmail(email, page, size, orderBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SearchedTransactionDTO> findById(@PathVariable Long id){
            return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    public ResponseEntity<SearchedTransactionDTO> create(@RequestBody @Valid CreateTransactionDTO transactionDTO){
            Transaction transaction = service.create(transactionDTO);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(transaction.getId()).toUri();
            return ResponseEntity.created(uri).body(SearchedTransactionDTO.from(transaction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SearchedTransactionDTO> update(@PathVariable Long id, @RequestBody @Valid UpdateTransactionDTO transactionDTO){
            SearchedTransactionDTO newTransation = service.update(id, transactionDTO);
            return ResponseEntity.ok(newTransation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
            return ResponseEntity.ok(service.delete(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Page<SearchedTransactionDTO>> findAllByUser(
            @PathVariable Long id,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "4") int size,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ){
        return ResponseEntity.ok(service.findAllByUser(id, page, size, orderBy, direction));
    }
}

