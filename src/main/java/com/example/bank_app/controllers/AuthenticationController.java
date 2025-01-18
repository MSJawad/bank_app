package com.example.bank_app.controllers;

import com.example.bank_app.auth.JwtUtil;
import com.example.bank_app.models.request.AuthenticationRequest;
import com.example.bank_app.models.response.AuthenticationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest authRequest) {
        // Validate credentials (replace this with a real validation mechanism)
        if ("user".equals(authRequest.getUsername()) && "password".equals(authRequest.getPassword())) {
            try {
                String token = JwtUtil.generateToken(authRequest.getUsername());
                return ResponseEntity.ok(new AuthenticationResponse(token));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}