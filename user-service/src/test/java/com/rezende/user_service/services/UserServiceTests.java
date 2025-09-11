package com.rezende.user_service.services;

import com.rezende.user_service.dto.RegisterCustomerDTO;
import com.rezende.user_service.dto.UserResponseDTO;
import com.rezende.user_service.entities.User;
import com.rezende.user_service.enums.AccountStatus;
import com.rezende.user_service.enums.RoleType;
import com.rezende.user_service.exceptions.EmailAlreadyExistsException;
import com.rezende.user_service.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    @DisplayName("Deve retornar o DTO do utilizador quando o ID existe")
    void findMeShouldReturnUserDTOWhenUserExists() {

        final String userIdString = "a4e1a4d8-1c7b-4b6f-8a2d-9e0c7b1b3a1d";
        final UUID userId = UUID.fromString(userIdString);
        final User mockUser = User.from(
                userId,
                "Utilizador Encontrado",
                "encontrado@email.com",
                "senha-codificada",
                RoleType.CUSTOMER,
                AccountStatus.ACTIVE
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        final UserResponseDTO result = userService.findMe(userIdString);

        assertNotNull(result);
        assertEquals(userIdString, result.id());
        assertEquals("Utilizador Encontrado", result.name());
        assertEquals("encontrado@email.com", result.email());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("Quando o email não existe, cria o usuário com sucesso")
    void shouldRegisterNewUserSuccessfully() {

        final UUID id = UUID.randomUUID();
        final RegisterCustomerDTO registerUser = RegisterCustomerDTO.from(
                "Fulano",
                "fulano@gmail.com",
                "123456"
        );

        final User savedUser = User.from(
                id,
                "Fulano",
                "fulano@gmail.com",
                "encoded-123456",
                RoleType.CUSTOMER,
                AccountStatus.ACTIVE
        );
        savedUser.setId(UUID.fromString("3e56c042-b325-4eb4-a8cb-d197c16f28d2"));

        when(userRepository.findByEmail(registerUser.email()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(registerUser.password()))
                .thenReturn("encoded-123456");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        final UserResponseDTO response = userService.register(registerUser);

        assertNotNull(response);
        assertEquals("3e56c042-b325-4eb4-a8cb-d197c16f28d2", response.id());
        assertEquals(registerUser.name(), response.name());
        assertEquals(registerUser.email(), response.email());
        assertEquals(RoleType.CUSTOMER, response.roleType());

        verify(userRepository).findByEmail(registerUser.email());
        verify(passwordEncoder).encode(registerUser.password());
        verify(userRepository, timeout(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Quando o email já existe, lança EmailAlreadyExistsException")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        final UUID id = UUID.randomUUID();
        final RegisterCustomerDTO registerUser = RegisterCustomerDTO.from(
                "Fulano",
                "fulano@gmail.com",
                "123456"
        );

        final User existingUser = User.from(
                id,
                "Fulano",
                "fulano@gmail.com",
                "encoded-123456",
                RoleType.CUSTOMER,
                AccountStatus.ACTIVE
        );
        existingUser.setId(UUID.fromString("3e56c042-b325-4eb4-a8cb-d197c16f28d2"));

        when(userRepository.findByEmail(registerUser.email()))
                .thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.register(registerUser))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("There is already a user with this email.");

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Verificar se a senha e criptografada antes de salvar")
    void shouldEncodePasswordBeforeSavingUser() {

        final RegisterCustomerDTO registerUser = RegisterCustomerDTO.from(
                "Fulano",
                "fulano@gmail.com",
                "123456"
        );

        when(userRepository.findByEmail(registerUser.email()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(registerUser.password()))
                .thenReturn("encoded-123456");

        final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture()))
                .thenAnswer(invocation -> {
                    final User user = invocation.getArgument(0);
                    user.setId(UUID.randomUUID());
                    return user;
                });

        final UserResponseDTO response = userService.register(registerUser);
        final User saved = userCaptor.getValue();

        assertNotNull(response);
        assertEquals("encoded-123456", saved.getPassword());

        verify(passwordEncoder).encode("123456");
    }

    @Test
    @DisplayName("Todo novo usuário registrado recebe o papel CUSTOMER")
    void shouldAssignCustomerRoleWhenRegisteringNewUser() {

        final RegisterCustomerDTO registerUser = RegisterCustomerDTO.from(
                "Fulano",
                "fulano@gmail.com",
                "123456"
        );

        when(userRepository.findByEmail(registerUser.email()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(registerUser.password()))
                .thenReturn("encoded-123456");

        final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture()))
                .thenAnswer(invocation -> {
                    final User user = invocation.getArgument(0);
                    user.setId(UUID.randomUUID());
                    return user;
                });

        final UserResponseDTO response = userService.register(registerUser);
        final User saved = userCaptor.getValue();

        assertNotNull(response);
        assertEquals(RoleType.CUSTOMER, response.roleType());
        assertEquals(RoleType.CUSTOMER, saved.getRoleType());

        verify(userRepository).findByEmail(registerUser.email());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder).encode("123456");
    }

    @Test
    @DisplayName("Todo novo usuário registrado recebe o status de conta ACTIVE")
    void shouldAssignActiveAccountStatusWhenRegisteringNewUser() {

        final RegisterCustomerDTO registerUser = RegisterCustomerDTO.from(
                "Fulano",
                "fulano@gmail.com",
                "123456"
        );

        when(userRepository.findByEmail(registerUser.email()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(registerUser.password()))
                .thenReturn("encoded-123456");

        final ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(userCaptor.capture()))
                .thenAnswer(invocation -> {
                    final User user = invocation.getArgument(0);
                    user.setId(UUID.randomUUID());
                    return user;
                });

        final UserResponseDTO response = userService.register(registerUser);
        final User saved = userCaptor.getValue();

        assertNotNull(response);
        assertEquals(AccountStatus.ACTIVE, response.accountStatus());
        assertEquals(AccountStatus.ACTIVE, saved.getAccountStatus());

        verify(userRepository).findByEmail(registerUser.email());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder).encode("123456");
    }
}
