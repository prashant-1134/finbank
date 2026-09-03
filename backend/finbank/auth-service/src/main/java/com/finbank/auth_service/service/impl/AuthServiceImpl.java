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
import com.finbank.auth_service.entity.RefreshToken;
import com.finbank.auth_service.service.RefreshTokenService;
import com.finbank.auth_service.dto.RefreshTokenRequest;
import com.finbank.auth_service.dto.RefreshTokenResponse;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService
    ){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
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

        String accessToken = jwtService.generateToken(user.getEmail(),
                user.getRole().name());

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        return new LoginResponse(accessToken, refreshToken.getToken(), "Bearer");
    }

    @Override
    public RefreshTokenResponse refreshAccessToken(
            RefreshTokenRequest request
    ){

        // 1. verify the old refresh token
        RefreshToken oldRefreshToken =  refreshTokenService.verifyRefreshToken(request.getRefreshToken());


        // 2. get the use from the old refresh token
        User user = oldRefreshToken.getUser();

        // 3. Revoke the old refresh token
        refreshTokenService.revokeRefreshToken(
                oldRefreshToken.getToken()
        );

        //4. create a new refresh token
        RefreshToken newRefreshToken =
                refreshTokenService.createRefreshToken(user);

        //5. generate new access token
        String accessToken = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );


        //6. return both refresh token as well as access token
        return new RefreshTokenResponse(
                accessToken,
                newRefreshToken.getToken(),
                "Bearer"
        );
    }

    @Override
    public void logout(String refreshToken)
    {

        refreshTokenService.revokeRefreshToken(refreshToken);
    }

    @Override
    public void logoutAll(String email){
        User user =  userRepository.findByEmail(email)
                .orElseThrow(()->
                        new InvalidCredentialsException("User Not Found"));

        refreshTokenService.revokeAllRefreshToken(user.getId());
    }


}
