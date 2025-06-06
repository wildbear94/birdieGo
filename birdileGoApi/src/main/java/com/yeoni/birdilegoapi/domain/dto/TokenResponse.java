package com.yeoni.birdilegoapi.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TokenResponse {

    private String grantType; // JWT에 대한 인증 타입, 일반적으로 "Bearer" 사용
    private String accessToken;
    private String refreshToken;

}
