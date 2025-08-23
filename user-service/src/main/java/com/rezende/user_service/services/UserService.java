package com.rezende.user_service.services;

import com.rezende.user_service.dto.CreateUserDTO;
import com.rezende.user_service.dto.UserResponse;
import com.rezende.user_service.entities.User;
import com.rezende.user_service.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserResponse insert(final CreateUserDTO userDTO) {

        final User user = new User();


    }
}
