package com.rezende.user_service.controllers;


import com.rezende.user_service.dto.*;
import com.rezende.user_service.services.AuthService;
import com.rezende.user_service.services.KeycloakClient;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final KeycloakClient keycloakClient;

    public AuthController(
            final KeycloakClient keycloakClient
    ) {
        this.keycloakClient = keycloakClient;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody final LoginRequestDTO dto) {
        final LoginResponseDTO response = this.keycloakClient.userLogin(dto);
        return ResponseEntity.ok(response);
    }


}
