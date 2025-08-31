package com.rezende.user_service.services;

import com.rezende.user_service.dto.UserResponseDTO;
import com.rezende.user_service.entities.User;
import com.rezende.user_service.exceptions.UserNotFoundException;
import com.rezende.user_service.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findMe(final String userId) {
        final User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() ->  new UserNotFoundException("User with " + userId + " not Found"));
        return UserResponseDTO.of(user);
    }
}
