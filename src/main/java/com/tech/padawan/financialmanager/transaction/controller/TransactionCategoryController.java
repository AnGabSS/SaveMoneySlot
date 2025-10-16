package com.tech.padawan.financialmanager.transaction.controller;

import com.tech.padawan.financialmanager.global.config.security.JwtTokenService;
import com.tech.padawan.financialmanager.transaction.dto.CreateTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.dto.SearchedTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.dto.UpdateTransactionCategoryDTO;
import com.tech.padawan.financialmanager.transaction.model.TransactionCategory;
import com.tech.padawan.financialmanager.transaction.service.ITransactionCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/transaction/category")
@Tag(
        name = "Transaction Categories",
        description = "Responsible for defining the different types of transactions, such as an expense with a car or money received from a freelance job."
)
public class TransactionCategoryController {


    private final ITransactionCategoryService service;
    private final JwtTokenService tokenService;

    public TransactionCategoryController(ITransactionCategoryService service, JwtTokenService tokenService ){
        this.service = service;
        this.tokenService = tokenService;
    }

    @Operation(
            summary = "Get user's transaction categories",
            description = "Returns a paginated list of all transaction categories created exclusively by the authenticated user. The user is identified via the JWT token provided in the Authorization header.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the page of user's transaction categories.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found.",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. Token is invalid, expired, or was not provided.",
                            content = @Content
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Page<SearchedTransactionCategoryDTO>> findAll(
            @Parameter(description = "Authentication JWT token. Must be prefixed with 'Bearer '.", required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9...")
            @RequestHeader("Authorization") String authorizationHeader,

            @Parameter(description = "The page number to retrieve, starting in page number 1.", example = "1")
            @RequestParam(value = "page", defaultValue = "0") int page,

            @Parameter(description = "The number of results per page.", example = "10")
            @RequestParam(value = "size", defaultValue = "4") int size,

            @Parameter(description = "The field to sort the results by.", example = "id")
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,

            @Parameter(description = "The sort direction ('ASC' for ascending, 'DESC' for descending).", schema = @Schema(type = "string", allowableValues = {"ASC", "DESC"}))
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ) {
        String jwtToken = authorizationHeader.substring(7);
        Long id = Long.parseLong(tokenService.getSubjectFromToken(jwtToken));
        return ResponseEntity.ok(service.findAllByUserId(id, page, size, orderBy, direction));
    }

    @Operation(
            summary = "Get a transaction category by ID",
            description = "Retrieves a single transaction category if it exists.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the category.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchedTransactionCategoryDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Category not found for the provided ID.",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<SearchedTransactionCategoryDTO> findById(
            @Parameter(description = "ID of the category to be retrieved.", required = true, example = "1")
            @PathVariable Long id
    ) {
            return ResponseEntity.ok(service.getById(id));

    }

    @Operation(
            summary = "Create a new transaction category",
            description = "Creates a new category linked to the authenticated user. The user's ID is extracted from the JWT.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Category created successfully. The 'Location' header contains the URL to the new resource.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchedTransactionCategoryDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request payload. Please check the required fields.",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized. The JWT is invalid, expired, or was not provided.",
                            content = @Content
                    )
            }
    )
    @PostMapping
    public ResponseEntity<SearchedTransactionCategoryDTO> create(
            @Parameter(description = "Authentication JWT token.", required = true)
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody @Valid CreateTransactionCategoryDTO categoryDTO
    ) {
            String jwtToken = authorizationHeader.substring(7);
            Long id = Long.parseLong(tokenService.getSubjectFromToken(jwtToken));
            TransactionCategory category = service.create(id, categoryDTO);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(category.getId()).toUri();
            return ResponseEntity.created(uri).body(SearchedTransactionCategoryDTO.from(category));
    }

    @Operation(
            summary = "Update an existing transaction category",
            description = "Updates the data of a specific category based on its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Category updated successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = SearchedTransactionCategoryDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Category not found for the provided ID.",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid data provided for the update.",
                            content = @Content
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<SearchedTransactionCategoryDTO> update(
            @Parameter(description = "ID of the category to be updated.", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody @Valid UpdateTransactionCategoryDTO categoryDTO
    ) {
            SearchedTransactionCategoryDTO newCategory = service.update(id, categoryDTO);
            return ResponseEntity.ok(newCategory);

    }

    @Operation(
            summary = "Delete a transaction category",
            description = "Permanently removes a category based on its ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Category deleted successfully.",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Category deleted successfully."))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Category not found for the provided ID.",
                            content = @Content
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @Parameter(description = "ID of the category to be deleted.", required = true, example = "1")
            @PathVariable Long id
    ) {
            return ResponseEntity.ok(service.delete(id));
    }
}
