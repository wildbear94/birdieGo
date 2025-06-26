package com.yeoni.birdilegoapi.controller;

import com.yeoni.birdilegoapi.domain.dto.CommonResponse;
import com.yeoni.birdilegoapi.domain.dto.auth.LoginRequest;
import com.yeoni.birdilegoapi.domain.dto.auth.LoginResponse;
import com.yeoni.birdilegoapi.domain.dto.auth.TokenResponse;
import com.yeoni.birdilegoapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<CommonResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {

        LoginResponse loginResponse = authService.login(loginRequest.getLoginId(), loginRequest.getPassword());

        CommonResponse<LoginResponse> response = CommonResponse.<LoginResponse>builder()
            .status(HttpStatus.OK.value())
            .code("SUCCESS")
            .message("로그인에 성공했습니다.")
            .data(loginResponse)
            .build();

        return ResponseEntity.ok(response);

    }

    /*
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {


    }
    */

}
