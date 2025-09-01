package com.rezende.user_service.services;

import com.rezende.user_service.dto.UserResponseDTO;
import com.rezende.user_service.dto.RegisterUser;
import com.rezende.user_service.entities.User;
import com.rezende.user_service.enums.AccountStatus;
import com.rezende.user_service.exceptions.EmailAlreadyExistsException;
import com.rezende.user_service.exceptions.UserNotFoundException;
import com.rezende.user_service.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakClientService keycloakClient;

    public UserService(
            final UserRepository userRepository,
            final PasswordEncoder passwordEncoder,
            final KeycloakClientService keycloakClient
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.keycloakClient = keycloakClient;
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findMe(final String userId) {
        final User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() ->  new UserNotFoundException("User with " + userId + " not Found"));
        return UserResponseDTO.of(user);
    }

    @Transactional
    public UserResponseDTO register(final RegisterUser dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent())
            throw new EmailAlreadyExistsException("There is already a user with this email.");

        final String keycloakId = keycloakClient.createUserInKeycloak(dto);

        User user = User.from(
                UUID.fromString(keycloakId),
                dto.getName(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getRoleType(),
                AccountStatus.ACTIVE
        );
        user = userRepository.save(user);

        return UserResponseDTO.of(user);
    }
}
