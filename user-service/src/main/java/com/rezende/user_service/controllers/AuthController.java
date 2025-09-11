package com.rezende.user_service.controllers;


import com.rezende.user_service.dto.*;
import com.rezende.user_service.services.KeycloakClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Endpoints para autenticação de utilizadores")
public class AuthController {

    private final KeycloakClientService keycloakClient;

    public AuthController(
            final KeycloakClientService keycloakClient
    ) {
        this.keycloakClient = keycloakClient;
    }

    @Operation(
            summary = "Autentica um utilizador",
            description = "Realiza login utilizando Keycloak e retorna tokens de acesso."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid final LoginRequestDTO dto) {
        final LoginResponseDTO response = this.keycloakClient.userLogin(dto);
        return ResponseEntity.ok(response);
    }

}
