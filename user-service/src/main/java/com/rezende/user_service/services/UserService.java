package com.rezende.user_service.services;

import com.rezende.user_service.dto.DeviceTokenRequestDTO;
import com.rezende.user_service.dto.UserResponseDTO;
import com.rezende.user_service.dto.RegisterUser;
import com.rezende.user_service.entities.DeviceToken;
import com.rezende.user_service.entities.User;
import com.rezende.user_service.enums.AccountStatus;
import com.rezende.user_service.events.DeviceRegisteredEvent;
import com.rezende.user_service.events.UserRegisterEvent;
import com.rezende.user_service.exceptions.EmailAlreadyExistsException;
import com.rezende.user_service.exceptions.UserNotFoundException;
import com.rezende.user_service.exceptions.UserRegistrationException;
import com.rezende.user_service.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakClientService keycloakClient;
    private final UserEventProducer userEventProducer;

    public UserService(
            final UserRepository userRepository,
            final PasswordEncoder passwordEncoder,
            final KeycloakClientService keycloakClient,
            final UserEventProducer userEventProducer
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.keycloakClient = keycloakClient;
        this.userEventProducer = userEventProducer;
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

        try {
            keycloakClient.assignRealmRoleToUser(keycloakId, dto.getRoleType());

            final User user = User.builder()
                    .id(UUID.fromString(keycloakId))
                    .firstName(dto.getFirstName())
                    .lastName(dto.getLastName())
                    .email(dto.getEmail())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .roleType(dto.getRoleType())
                    .accountStatus(AccountStatus.ACTIVE)
                    .build();

            final User userSave = userRepository.save(user);
            keycloakClient.sendVerificationEmail(keycloakId);
            userEventProducer.sendUserCreatedEvent(UserRegisterEvent.of(userSave));

            return UserResponseDTO.of(userSave);

        } catch (Exception e) {
            keycloakClient.deleteUserInKeycloak(keycloakId);
            throw new UserRegistrationException("Could not save user locally. Reverting Keycloak creation.");
        }
    }

    public void registerDeviceToken(final String userId, final DeviceTokenRequestDTO request) {

        final User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User with id %s not found"));

        final DeviceToken deviceToken = DeviceToken.builder()
                .token(request.deviceToken())
                .user(user)
                .build();

        user.getDeviceTokens().add(deviceToken);
        userRepository.save(user);

        final DeviceRegisteredEvent event = new DeviceRegisteredEvent(userId, request.deviceToken(), Instant.now());
        userEventProducer.sendRegisterDevice(event);
    }
}
