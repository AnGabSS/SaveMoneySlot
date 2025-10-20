package com.tech.padawan.financialmanager.user.controller;

import com.tech.padawan.financialmanager.user.dto.*;
import com.tech.padawan.financialmanager.user.model.User;
import com.tech.padawan.financialmanager.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@Tag(
        name = "Users",
        description = "Endpoints for manage the user data."
)
public class UserController {

    private final IUserService service;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(IUserService service){
        this.service = service;
    }

    @Operation(
            summary = "Get a paginated list of users",
            description = "Retrieves a list of all users with support for pagination and sorting.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the page of users.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class))
                    )
            }
    )
    @GetMapping()
    public ResponseEntity<Page<UserSearchedDTO>> findAll(
            @Parameter(description = "The page number to retrieve, starting from page 1.", example = "1")
            @RequestParam(value = "page", defaultValue = "1") int page,

            @Parameter(description = "The number of results per page.")
            @RequestParam(value = "size", defaultValue = "10") int size,

            @Parameter(description = "The field to sort the results by (e.g., 'id', 'name').")
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,

            @Parameter(description = "The sort direction ('ASC' for ascending or 'DESC' for descending).")
            @RequestParam(value = "direction", defaultValue = "ASC") String direction
    ){
        if (page >= 0){
            logger.warn("Page number must to be greater than 0");
        }
        return ResponseEntity.ok().body(service.listAll(page, size, orderBy, direction));
    }

    @Operation(
            summary = "Get a user by ID",
            description = "Retrieves a single user by their unique ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved the user.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserSearchedDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found for the provided ID.",
                            content = @Content
                    )
            }
    )
    @GetMapping("/find-by-id/{id}")
    public ResponseEntity<UserSearchedDTO> findById(
            @Parameter(description = "ID of the user to be retrieved.", required = true, example = "1")
            @PathVariable Long id
    ){
        return ResponseEntity.ok().body(service.getById(id));
    }

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user in the system. The user's ID is generated automatically.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "User created successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserSearchedDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request payload. Check for missing or invalid fields.",
                            content = @Content
                    )
            }
    )
    @PostMapping
    public ResponseEntity<UserSearchedDTO> save(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data for the new user to be created.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateUserDTO.class))
            )
            @RequestBody @Valid CreateUserDTO user
    ){
        User userCreated = service.create(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userCreated.getId()).toUri();
        UserSearchedDTO userDTO = UserSearchedDTO.from(userCreated);
        return ResponseEntity.created(uri).body(userDTO);
    }

    @Operation(
            summary = "Update an existing user",
            description = "Updates the details of an existing user identified by their ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User updated successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserSearchedDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request payload.",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found for the provided ID.",
                            content = @Content
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<UserSearchedDTO> update(
            @Parameter(description = "ID of the user to be updated.", required = true, example = "1")
            @PathVariable Long id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Updated data for the user.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateUserDTO.class))
            )
            @RequestBody @Valid UpdateUserDTO user
    ){
        User userCreated = service.update(id, user);
        UserSearchedDTO userDTO = UserSearchedDTO.from(userCreated);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(
            summary = "Delete a user",
            description = "Permanently deletes a user from the system by their ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User deleted successfully.",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "User deleted successfully"))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found for the provided ID.",
                            content = @Content
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "ID of the user to be deleted.", required = true, example = "1")
            @PathVariable Long id
    ){
        return ResponseEntity.ok(service.delete(id));
    }

}
