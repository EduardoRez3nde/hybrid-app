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
@RequestMapping("/api/users")
public class UserController {

    private final AuthService userService;

    public UserController(
            final AuthService userService
    ) {
        this.userService = userService;
    }

    @PostMapping("/register/customer")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody final RegisterCustomerDTO dto) {
        return buildCreatedResponse(dto);
    }

    @PostMapping("/register/driver")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody final RegisterDriverDTO dto) {
        return buildCreatedResponse(dto);
    }

    private ResponseEntity<RegisterResponseDTO> buildCreatedResponse(final RegisterUser dto) {
        RegisterResponseDTO response = userService.register(dto);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        return ResponseEntity.created(uri).body(response);
    }

}
