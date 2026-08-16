package com.finbank.auth_service.service.impl;

import com.finbank.auth_service.dto.UserRequest;
import com.finbank.auth_service.dto.UserResponse;
import com.finbank.auth_service.entity.Role;
import com.finbank.auth_service.entity.User;
import com.finbank.auth_service.repository.UserRepository;
import com.finbank.auth_service.service.UserService;
import org.springframework.stereotype.Service;
import com.finbank.auth_service.exception.DuplicateEmailException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service

public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    /* constructor injection */

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse registerUser(UserRequest request){

        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateEmailException(
                    "Email already registered: " + request.getEmail()
            );
        }


        User user = new User();

        user.setFullName(request.getFullName());

        user.setEmail(request.getEmail());

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        user.setPassword(encodedPassword);

        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);

        UserResponse response = new UserResponse();

        response.setId(savedUser.getId());

        response.setFullName(savedUser.getFullName());

        response.setEmail(savedUser.getEmail());

        return response;
    }
}
