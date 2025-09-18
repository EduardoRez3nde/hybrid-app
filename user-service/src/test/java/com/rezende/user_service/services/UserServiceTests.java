package com.rezende.user_service.services;

import com.rezende.user_service.dto.RegisterCustomerDTO;
import com.rezende.user_service.dto.RegisterUser;
import com.rezende.user_service.dto.UserResponseDTO;
import com.rezende.user_service.entities.User;
import com.rezende.user_service.enums.AccountStatus;
import com.rezende.user_service.enums.RoleType;
import com.rezende.user_service.events.UserRegisterEvent;
import com.rezende.user_service.exceptions.EmailAlreadyExistsException;
import com.rezende.user_service.exceptions.UserRegistrationException;
import com.rezende.user_service.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private KeycloakClientService keycloakClient;

    @Mock
    private UserEventProducer userEventProducer;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Deve retornar o DTO do utilizador quando o ID existe")
    void findMeShouldReturnUserDTOWhenUserExists() {

        final String userIdString = "a4e1a4d8-1c7b-4b6f-8a2d-9e0c7b1b3a1d";
        final UUID userId = UUID.fromString(userIdString);
        final User mockUser = User.from(
                userId,
                "Utilizador",
                "Encontrado",
                "encontrado@email.com",
                "senha-codificada",
                RoleType.CUSTOMER,
                AccountStatus.ACTIVE
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        final UserResponseDTO result = userService.findMe(userIdString);

        assertNotNull(result);
        assertEquals(userIdString, result.id());
        assertEquals("Utilizador", result.firstName());
        assertEquals("Utilizador", result.lastName());
        assertEquals("encontrado@email.com", result.email());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Deve criar um utilizador com sucesso")
    void registerShouldCreateUserSuccessfully() {

        final RegisterUser dto = RegisterCustomerDTO.from("Fulano", "da Silva", "fulano@email.com", "senhaForte123");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(keycloakClient.createUserInKeycloak(dto)).thenReturn("123e4567-e89b-12d3-a456-426614174000");
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final UserResponseDTO response = userService.register(dto);

        assertNotNull(response);
        assertEquals(dto.getFirstName(), response.firstName());
        assertEquals(dto.getLastName(), response.lastName());
        assertEquals(dto.getEmail(), response.email());

        verify(userEventProducer, times(1)).sendUserCreatedEvent(any(UserRegisterEvent.class));
        verify(keycloakClient, times(1)).assignRealmRoleToUser("123e4567-e89b-12d3-a456-426614174000", dto.getRoleType());
        verify(keycloakClient, times(1)).sendVerificationEmail("123e4567-e89b-12d3-a456-426614174000");
    }

    @Test
    @DisplayName("Deve lançar EmailAlreadyExistsException quando o e-mail já existir")
    void registerShouldThrowExceptionWhenEmailAlreadyExists() {

        final RegisterUser dto = RegisterCustomerDTO.from("Fulano", "da Silva", "fulano@email.com", "senhaForte123");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(mock(User.class)));

        assertThrows(EmailAlreadyExistsException.class, () -> userService.register(dto));

        verify(keycloakClient, never()).createUserInKeycloak(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve reverter a criação no Keycloak e lançar UserRegistrationException quando falha ao salvar")
    void registerShouldRollbackAndThrowExceptionWhenSaveFails() {

        final RegisterUser dto = RegisterCustomerDTO.from("Fulano", "da Silva", "fulano@email.com", "senhaForte123");

        when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(keycloakClient.createUserInKeycloak(dto)).thenReturn("123e4567-e89b-12d3-a456-426614174000");
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB failure"));

        final UserRegistrationException exception = assertThrows(UserRegistrationException.class, () -> userService.register(dto));

        assertTrue(exception.getMessage().contains("Could not save user locally"));
        verify(keycloakClient, times(1)).deleteUserInKeycloak("123e4567-e89b-12d3-a456-426614174000");
    }

    @Test
    @DisplayName("Deve retornar o DTO do utilizador quando o ID existe")
    void findMeShouldReturnUserDTOWhenIdExists() {

        final String userId = "123e4567-e89b-12d3-a456-426614174000";
        final User user = User.from(UUID.fromString(userId), "Fulano", "da Silva", "fulano@email.com", "senha", RoleType.CUSTOMER, AccountStatus.ACTIVE);

        when(userRepository.findById(UUID.fromString(userId))).thenReturn(Optional.of(user));

        final UserResponseDTO response = userService.findMe(userId);

        assertNotNull(response);
        assertEquals(user.getFirstName(), response.firstName());
        assertEquals(user.getLastName(), response.lastName());
        assertEquals(user.getEmail(), response.email());
    }
}
