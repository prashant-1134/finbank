package com.finbank.auth_service.controller;

import com.finbank.auth_service.dto.UserRequest;
import com.finbank.auth_service.dto.UserResponse;
import com.finbank.auth_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}