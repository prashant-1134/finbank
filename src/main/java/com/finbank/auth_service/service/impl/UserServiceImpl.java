package com.finbank.auth_service.service.impl;

import com.finbank.auth_service.dto.UserRequest;
import com.finbank.auth_service.dto.UserResponse;
import com.finbank.auth_service.entity.User;
import com.finbank.auth_service.repository.UserRepository;
import com.finbank.auth_service.service.UserService;
import org.springframework.stereotype.Service;
import com.finbank.auth_service.exception.DuplicateEmailException;

@Service

public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    /* constructor injection */

    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
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

        user.setPassword(request.getPassword());

        User savedUser = userRepository.save(user);

        UserResponse response = new UserResponse();

        response.setId(savedUser.getId());

        response.setFullName(savedUser.getFullName());

        response.setEmail(savedUser.getEmail());

        return response;
    }
}
