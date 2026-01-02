package com.sanjay.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sanjay.dto.LoginRequest;
import com.sanjay.dto.LoginResponse;
import com.sanjay.dto.RegisterResponse;
import com.sanjay.dto.RegistrationRequest;
import com.sanjay.exception.QuickRentalException;
import com.sanjay.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegistrationRequest req)
            throws QuickRentalException {

        RegisterResponse response = authService.register(req);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req)
            throws QuickRentalException {

        LoginResponse response = authService.login(req);
        return ResponseEntity.ok(response);
    }

}
