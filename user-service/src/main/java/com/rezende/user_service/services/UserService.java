package com.rezende.user_service.services;

import com.rezende.user_service.dto.RegisterUserDTO;
import com.rezende.user_service.dto.UserResponseDTO;
import com.rezende.user_service.entities.User;
import com.rezende.user_service.entities.enums.RoleType;
import com.rezende.user_service.exceptions.EmailAlreadyExistsException;
import com.rezende.user_service.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            final UserRepository userRepository,
            final PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO register(final RegisterUserDTO userDTO) {

        if (userRepository.findByEmail(userDTO.email()).isPresent())
            throw new EmailAlreadyExistsException("There is already a user with this email.");

        User user = User.from(
                userDTO.name(),
                userDTO.email(),
                passwordEncoder.encode(userDTO.password()),
                RoleType.CUSTOMER
        );
        user = userRepository.save(user);

        return UserResponseDTO.of(user);
    }
}
