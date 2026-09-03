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
import com.finbank.auth_service.entity.Role;
import com.finbank.auth_service.dto.ProfileUpdateRequest;
import com.finbank.auth_service.dto.ChangePasswordRequest;


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

        response.setRole(savedUser.getRole());

        return response;
    }

    @Override
    public UserResponse getCurrentUser(String email){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        return response;

    }

    @Override
    public UserResponse updateProfile(
            String currEmail,
            ProfileUpdateRequest request
    ){
        User user = userRepository.findByEmail(currEmail)
                .orElseThrow(()->
                        new RuntimeException("User not found")
                 );

        /*
        * If mail is changed we have to make sure it is not used by other user*/

        System.out.println("Current email: " + currEmail);
        System.out.println("Requested email: " + request.getEmail());
        System.out.println(
                "Email exists: " +
                        userRepository.existsByEmail(request.getEmail())
        );
        System.out.println(
                "Is mail match : " +
                        currEmail.equals(request.getEmail())
        );
        if(!currEmail.equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateEmailException(
                    "Email already registered " + request.getEmail()
            );
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        User updatedUser = userRepository.save(user);

        UserResponse response = new UserResponse();

        response.setId(updatedUser.getId());
        response.setFullName(updatedUser.getFullName());
        response.setEmail(updatedUser.getEmail());
        response.setRole(updatedUser.getRole());

        return response;

    }

    @Override
    public void changePassword(String currentEmail,
                               ChangePasswordRequest request){
        User user  = userRepository.findByEmail(currentEmail)
                .orElseThrow(()->new RuntimeException("User Not Found")
                );

        // verify the current password
        if(!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )){
            throw new RuntimeException("Password is Incorrect");
        }

        // Encode and save the new password

        String encodedPassword =
                passwordEncoder.encode(request.getNewPassword());

        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

}
