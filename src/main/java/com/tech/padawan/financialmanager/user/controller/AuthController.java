package com.tech.padawan.financialmanager.user.controller;

import com.tech.padawan.financialmanager.user.dto.LoginUserDTO;
import com.tech.padawan.financialmanager.user.dto.RecoveryJwtTokenDTO;
import com.tech.padawan.financialmanager.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(
        name = "User Authentication",
        description = "Endpoints for user authentication, including login to obtain a JWT token."
)
public class AuthController {

    private final IUserService _service;

    public AuthController(IUserService service){
        this._service = service;
    }

    @Operation(
            summary = "Authenticate a user",
            description = "Authenticates a user based on their email and password. If the credentials are valid, it returns a JWT token for authorizing subsequent requests.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Object containing the user's login credentials.",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginUserDTO.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User authenticated successfully.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecoveryJwtTokenDTO.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid credentials. The provided email or password is incorrect.",
                            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string", example = "Email or password is incorrect"))
                    )
            }
    )
    @PostMapping()
    public ResponseEntity<Object> authenticateUser(@RequestBody LoginUserDTO user){
        try{
            RecoveryJwtTokenDTO token = _service.authenticateUser(user);
            return ResponseEntity.ok(token);
        } catch (BadCredentialsException | InternalAuthenticationServiceException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email or password is incorrect");
        }
    }

}
