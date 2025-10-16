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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping("/api/v1/transaction")
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

    @Operation(
            summary = "Get user's transactions",
            description = "Retrieves a paginated list of all transactions belonging to the authenticated user. The list can be filtered by a search term.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the page of transactions.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized. Token is invalid or missing.", content = @Content)
            }
    )
    @GetMapping
    public ResponseEntity<Page<SearchedTransactionDTO>> findAll(
            @Parameter(description = "Authentication JWT token.", required = true)
            @RequestHeader("Authorization") String authorizationHeader,

            @Parameter(description = "Search term to filter transactions by description.")
            @RequestParam(value = "search", defaultValue = "") String search,

            @Parameter(description = "The page number to retrieve, starting in page number 1.", example = "1")
            @RequestParam(value = "page", defaultValue = "0") int page,

            @Parameter(description = "The number of results per page.")
            @RequestParam(value = "size", defaultValue = "10") int size,

            @Parameter(description = "The field to sort the results by.")
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,

            @Parameter(description = "The sort direction ('ASC' or 'DESC').")
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ){
        String jwtToken = authorizationHeader.substring(7);
        Long id = Long.parseLong(tokenService.getSubjectFromToken(jwtToken));
        return ResponseEntity.ok(service.findAllByUserId(id, search, page, size, orderBy, direction));
    }

    @Operation(
            summary = "Get a transaction by ID",
            description = "Retrieves a single transaction by its unique ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the transaction.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchedTransactionDTO.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Transaction not found for the provided ID.", content = @Content)
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<SearchedTransactionDTO> findById(
            @Parameter(description = "ID of the transaction to be retrieved.", required = true, example = "1") @PathVariable Long id
    ){
            return ResponseEntity.ok(service.getById(id));
    }

    @Operation(
            summary = "Create a new transaction",
            description = "Creates a new transaction for the authenticated user. The user is identified via the JWT token.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Transaction created successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchedTransactionDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload.", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized. Token is invalid or missing.", content = @Content)
            }
    )
    @PostMapping
    public ResponseEntity<SearchedTransactionDTO> create(
            @Parameter(description = "Authentication JWT token.", required = true)
            @RequestHeader("Authorization") String authorizationHeader,

            @RequestBody @Valid CreateTransactionDTO transactionDTO
    ){
            String jwtToken = authorizationHeader.substring(7);
            Long id = Long.parseLong(tokenService.getSubjectFromToken(jwtToken));
            Transaction transaction = service.create(id, transactionDTO);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(transaction.getId()).toUri();
            return ResponseEntity.created(uri).body(SearchedTransactionDTO.from(transaction));
    }

    @Operation(
            summary = "Update an existing transaction",
            description = "Updates the details of an existing transaction by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Transaction updated successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchedTransactionDTO.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Transaction not found for the provided ID.", content = @Content)
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<SearchedTransactionDTO> update(
            @Parameter(description = "ID of the transaction to be updated.", required = true, example = "1")
            @PathVariable Long id,

            @RequestBody @Valid UpdateTransactionDTO transactionDTO
    ){
            SearchedTransactionDTO newTransation = service.update(id, transactionDTO);
            return ResponseEntity.ok(newTransation);
    }

    @Operation(
            summary = "Delete a transaction",
            description = "Permanently deletes a transaction by its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Transaction deleted successfully.",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))
                    ),
                    @ApiResponse(responseCode = "404", description = "Transaction not found for the provided ID.", content = @Content)
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @Parameter(description = "ID of the transaction to be deleted.", required = true, example = "1")
            @PathVariable Long id
    ){
            return ResponseEntity.ok(service.delete(id));
    }

}

