package com.rezende.user_service.controllers;


import com.rezende.user_service.dto.CreateUserDTO;
import com.rezende.user_service.dto.UserResponse;
import com.rezende.user_service.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> insert(@RequestBody final CreateUserDTO dto) {

    }
}
