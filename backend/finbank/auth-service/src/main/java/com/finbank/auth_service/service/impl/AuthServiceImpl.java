package com.finbank.auth_service.service.impl;

import com.finbank.auth_service.dto.LoginRequest;
import com.finbank.auth_service.dto.LoginResponse;
import com.finbank.auth_service.entity.User;
import com.finbank.auth_service.repository.UserRepository;
import com.finbank.auth_service.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.finbank.auth_service.security.JwtService;
import com.finbank.auth_service.exception.InvalidCredentialsException;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponse login(LoginRequest request){
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"
                        ))
                ;

        boolean passwordMatches  = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if(!passwordMatches){
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(user.getEmail());

        return new LoginResponse(token, "Bearer");
    }


}
