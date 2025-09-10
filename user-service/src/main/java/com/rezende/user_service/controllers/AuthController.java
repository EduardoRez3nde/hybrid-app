package com.rezende.user_service.controllers;


import com.rezende.user_service.dto.*;
import com.rezende.user_service.services.KeycloakClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KeycloakClientService keycloakClient;

    public AuthController(
            final KeycloakClientService keycloakClient
    ) {
        this.keycloakClient = keycloakClient;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody final LoginRequestDTO dto) {
        final LoginResponseDTO response = this.keycloakClient.userLogin(dto);
        return ResponseEntity.ok(response);
    }


}
