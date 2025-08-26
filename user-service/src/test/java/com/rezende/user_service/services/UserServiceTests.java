package com.rezende.user_service.services;

import com.rezende.user_service.dto.RegisterUserDTO;
import com.rezende.user_service.dto.UserResponseDTO;
import com.rezende.user_service.entities.User;
import com.rezende.user_service.entities.enums.RoleType;
import com.rezende.user_service.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Quando o email não existe, cria o usuário com sucesso")
    void shouldRegisterNewUserSuccessfully() {

        final RegisterUserDTO registerUser = RegisterUserDTO.from(
                "Fulano",
                "fulano@gmail.com",
                "123456"
        );

        final User savedUser = User.from(
                "Fulano",
                "fulano@gmail.com",
                "encoded-123456",
                RoleType.CUSTOMER
        );
        savedUser.setId(UUID.fromString("3e56c042-b325-4eb4-a8cb-d197c16f28d2"));

        when(userRepository.findByEmail(registerUser.email()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(registerUser.password()))
                .thenReturn("encoded-123456");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        final UserResponseDTO response = userService.register(registerUser);

        Assertions.assertNotNull(response);
        Assertions.assertEquals("3e56c042-b325-4eb4-a8cb-d197c16f28d2", response.id());
        Assertions.assertEquals(registerUser.name(), response.name());
        Assertions.assertEquals(registerUser.email(), response.email());
        Assertions.assertEquals(RoleType.CUSTOMER, response.roleType());

        verify(userRepository).findByEmail(registerUser.email());
        verify(passwordEncoder).encode(registerUser.password());
        verify(userRepository, timeout(1)).save(any(User.class));
    }
}
