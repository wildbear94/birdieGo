package com.yeoni.birdilegoapi.controller;

import com.yeoni.birdilegoapi.domain.dto.auth.LoginRequest;
import com.yeoni.birdilegoapi.domain.dto.auth.TokenResponse;
import com.yeoni.birdilegoapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {

        TokenResponse tokenResponse = authService.login(loginRequest.getLoginId(), loginRequest.getPassword());
        return ResponseEntity.ok(tokenResponse);

    }

    /*
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {


    }
    */

}
