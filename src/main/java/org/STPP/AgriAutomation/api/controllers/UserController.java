package org.STPP.AgriAutomation.api.controllers;

import org.STPP.AgriAutomation.api.services.UserService;
import org.STPP.AgriAutomation.data.dtos.auth.AuthResponse;
import org.STPP.AgriAutomation.data.dtos.auth.LoginRequest;
import org.STPP.AgriAutomation.data.dtos.auth.RegisterRequest;
import org.STPP.AgriAutomation.data.entities.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService service;


    @PostMapping("/register")
    public ResponseEntity<Users> register(@Validated @RequestBody RegisterRequest registerRequest) {
        Users user = service.register(registerRequest);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Validated @RequestBody LoginRequest loginRequest) {
        String token = service.verify(loginRequest);
        if ("fail".equals(token)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
