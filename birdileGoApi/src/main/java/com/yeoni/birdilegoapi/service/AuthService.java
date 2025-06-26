package com.yeoni.birdilegoapi.service;

import com.yeoni.birdilegoapi.domain.dto.auth.LoginResponse;
import com.yeoni.birdilegoapi.domain.dto.auth.RefreshTokenResponse;
import com.yeoni.birdilegoapi.domain.dto.auth.TokenResponse;
import com.yeoni.birdilegoapi.domain.entity.User;
import com.yeoni.birdilegoapi.exception.CustomException;
import com.yeoni.birdilegoapi.exception.ErrorCode;
import com.yeoni.birdilegoapi.jwt.JwtTokenProvider;
import com.yeoni.birdilegoapi.mapper.RefreshTokenMapper;
import com.yeoni.birdilegoapi.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
    //private final AuthenticationManager authenticationManager;
    private final RefreshTokenMapper refreshTokenMapper;
    private final UserMapper userMapper;
    //private long refreshTokenValidityInMs;

    public LoginResponse login(String loginId, String password) {
        // 1. 로그인 ID/PW 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginId, password);


        Authentication authentication;
        try {
            // 2. 실제 검증 (비밀번호 확인 등)
            // 이 과정에서 CustomUserDetailsService의 loadUserByUsername 메서드가 호출됩니다.
            authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            // 인증 실패 시 (아이디가 없거나, 비밀번호가 틀림) CustomException 발생
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }


        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenResponse tokenResponse = jwtTokenProvider.generateToken(authentication);

        // 4. Refresh Token 처리
        // 인증 객체(Authentication)에서 사용자 정보를 직접 가져와 불필요한 DB 조회를 제거합니다.
        //User user = (User) authentication.getPrincipal();
        User currentUser = userMapper.findByLoginId(loginId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));



        // 기존의 리프레시 토큰이 있다면 삭제 (정책: 새 로그인 시 이전 세션 무효화)
        refreshTokenMapper.deleteByUserId(currentUser.getUserId());

        // Refresh Token 객체 생성
        RefreshTokenResponse refreshToken = RefreshTokenResponse.builder()
            .userId(currentUser.getUserId())
            .token(tokenResponse.getRefreshToken())
            // 하드코딩된 만료 시간 대신 설정 파일(application.yml)에서 주입받은 값을 사용합니다.
            .expiresAt(new Timestamp(System.currentTimeMillis() + 86400 * 1000L))
            .build();

        // (중요) Refresh Token을 DB에 저장하는 로직을 활성화합니다.
        refreshTokenMapper.save(refreshToken);

        currentUser.setPassword(null);



        return LoginResponse.builder()
            .tokenInfo(tokenResponse)
            .userInfo(currentUser)
            .build();
    }
}