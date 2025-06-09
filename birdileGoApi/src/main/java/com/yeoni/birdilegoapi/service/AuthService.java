package com.yeoni.birdilegoapi.service;

import com.yeoni.birdilegoapi.domain.dto.auth.RefreshTokenResponse;
import com.yeoni.birdilegoapi.domain.dto.auth.TokenResponse;
import com.yeoni.birdilegoapi.domain.entity.User;
import com.yeoni.birdilegoapi.jwt.JwtTokenProvider;
import com.yeoni.birdilegoapi.mapper.RefreshTokenMapper;
import com.yeoni.birdilegoapi.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenMapper refreshTokenMapper;
    private final UserMapper userMapper; // 사용자 ID를 얻기 위해 추가

    public TokenResponse login(String loginId, String password) {
        //1. 로그인 ID/PW 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginId, password);

        // 2. 실제 검증 (비밀번호 확인 등)
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenResponse tokenResponse = jwtTokenProvider.generateToken(authentication);

        // 4. Refresh Token을 DB에 저장
        User user = userMapper.findByLoginId(loginId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // 기존의 리프레시 토큰이 있다면 삭제 (선택적: 동시 로그인을 하나로 제한)
        refreshTokenMapper.deleteByUserId(user.getUserId());

        RefreshTokenResponse refreshToken = RefreshTokenResponse.builder()
            .userId(user.getUserId())
            .token(tokenResponse.getRefreshToken())
            .expiresAt(new Timestamp(System.currentTimeMillis() + 86400 * 1000L)) // yml 설정값과 일치
            .build();
        refreshTokenMapper.save(refreshToken);

        return tokenResponse;
    }


}
