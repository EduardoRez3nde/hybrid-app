package com.rezende.user_service.services;

import com.rezende.user_service.dto.RegisterResponseDTO;
import com.rezende.user_service.dto.RegisterUser;
import com.rezende.user_service.entities.User;
import com.rezende.user_service.entities.enums.AccountStatus;
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
    public RegisterResponseDTO register(final RegisterUser dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent())
            throw new EmailAlreadyExistsException("There is already a user with this email.");

        User user = User.from(
                dto.getName(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getRoleType(),
                AccountStatus.ACTIVE
        );
        user = userRepository.save(user);

        return RegisterResponseDTO.of(user);
    }
}
