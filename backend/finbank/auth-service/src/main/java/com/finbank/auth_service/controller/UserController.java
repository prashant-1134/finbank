package com.finbank.auth_service.controller;

import com.finbank.auth_service.dto.UserRequest;
import com.finbank.auth_service.dto.UserResponse;
import com.finbank.auth_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import com.finbank.auth_service.dto.ProfileUpdateRequest;
import com.finbank.auth_service.dto.ChangePasswordRequest;
@RestController
@RequestMapping("/api/users")
public class UserController{
    private final UserService userService;

    /* Constructor injection*/
    public UserController(UserService userService){
        this.userService  = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody UserRequest request
    ){
        UserResponse response = userService.registerUser(request);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            Authentication authentication
    ) {
//        return ResponseEntity.ok(
//                "Logged in as: " + authentication.getName()
//        );

        UserResponse response = userService.getCurrentUser(authentication.getName());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody ProfileUpdateRequest request
    ) {

        UserResponse response =
                userService.updateProfile(
                        authentication.getName(),
                        request
                );

        return ResponseEntity.ok(response);
    }

    @PutMapping("change-password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ){
        userService.changePassword(authentication.getName(), request);

        return ResponseEntity.noContent().build();
    }


}