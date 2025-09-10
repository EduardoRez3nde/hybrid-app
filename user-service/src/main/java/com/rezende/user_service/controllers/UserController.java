package com.rezende.user_service.controllers;


import com.rezende.user_service.dto.*;
import com.rezende.user_service.events.UserRegisterEvent;
import com.rezende.user_service.services.UserEventProducer;
import com.rezende.user_service.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(
            final UserService userService,
            final UserEventProducer userEventProducer
    ) {
        this.userService = userService;
    }

    @PostMapping("/register/customer")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody final RegisterCustomerDTO dto) {
        return buildCreatedResponse(dto);
    }

    @PostMapping("/register/driver")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody final RegisterDriverDTO dto) {
        return buildCreatedResponse(dto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getMyProfile(@RequestHeader("X-User-ID") final String userId) {
        final UserResponseDTO response = userService.findMe(userId);
        return ResponseEntity.ok(response);
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
