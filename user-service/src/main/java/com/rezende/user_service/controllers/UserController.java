package com.rezende.user_service.controllers;


import com.rezende.user_service.dto.*;
import com.rezende.user_service.services.UserEventProducer;
import com.rezende.user_service.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "Endpoints para registo e gestão de utilizadores")
public class UserController {

    private final UserService userService;

    public UserController(
            final UserService userService,
            final UserEventProducer userEventProducer
    ) {
        this.userService = userService;
    }

    @Operation(summary = "Regista um novo utilizador do tipo 'CUSTOMER'",
            description = "Cria uma nova identidade no Keycloak e um perfil local para um novo cliente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilizador registado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "O e-mail fornecido já existe"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping("/register/customer")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody final RegisterCustomerDTO dto) {
        return buildCreatedResponse(dto);
    }

    @Operation(summary = "Regista um novo utilizador do tipo 'DRIVER'",
            description = "Cria uma nova identidade no Keycloak e um perfil local para um novo motorista.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilizador registado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "O e-mail fornecido já existe"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @PostMapping("/register/driver")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody final RegisterDriverDTO dto) {
        return buildCreatedResponse(dto);
    }

    @Operation(summary = "Obtém o perfil do utilizador autenticado",
            description = "Retorna os dados do utilizador correspondente ao token JWT fornecido.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Utilizador não encontrado na base de dados local"),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile(
            @RequestHeader("X-User-ID") final String userId
    ) {
        final UserResponseDTO response = userService.findMe(userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/register-device")
    public ResponseEntity<Void> registerDevice(
            @RequestHeader("X-User-ID") final String userId,
            @RequestBody final DeviceTokenRequestDTO request
    ) {
        userService.registerDeviceToken(userId, request);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<UserResponseDTO> buildCreatedResponse(final RegisterUser dto) {
        final UserResponseDTO response = userService.register(dto);
        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }
}
