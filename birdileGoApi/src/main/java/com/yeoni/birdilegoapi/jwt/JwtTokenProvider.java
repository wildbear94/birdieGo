package com.yeoni.birdilegoapi.jwt;

import com.yeoni.birdilegoapi.domain.dto.auth.TokenResponse;
import com.yeoni.birdilegoapi.exception.CustomException;
import com.yeoni.birdilegoapi.exception.ErrorCode;
import com.yeoni.birdilegoapi.mapper.UserMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;


@Slf4j
@Component
public class JwtTokenProvider {

    private final String secretKey;
    private final long accessTokenValidityInSeconds;
    private final long refreshTokenValidityInSeconds;
    private final UserMapper userMapper;
    private Key key;

    // application.yml에서 설정한 값들을 주입받음
    public JwtTokenProvider(
        @Value("${jwt.secret-key}") String secretKey,
        @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidity,
        @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidity, UserMapper userMapper) {
        this.secretKey = secretKey;
        this.accessTokenValidityInSeconds = accessTokenValidity * 1000; // 밀리초로 변환
        this.refreshTokenValidityInSeconds = refreshTokenValidity * 1000; // 밀리초로 변환
        this.userMapper = userMapper;
    }

    // 객체 초기화: secretKey를 Base64로 디코딩하여 Key 객체 생성
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 인증 정보를 기반으로 Access Token과 Refresh Token을 생성하는 메서드
     * @param authentication Spring Security의 인증 정보
     * @return TokenInfo (생성된 토큰 정보)
     */
    public TokenResponse generateToken(Authentication authentication) {
        // 권한 정보(role)를 문자열로 변환
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + accessTokenValidityInSeconds);
        String accessToken = Jwts.builder()
            .setSubject(authentication.getName())       // Subject: 사용자 이름 (login_id)
            .claim("auth", authorities)                 // Claim: 권한 정보
            .setExpiration(accessTokenExpiresIn)        // 만료 시간
            .signWith(key, SignatureAlgorithm.HS256)    // 서명 알고리즘
            .compact();

        // Refresh Token 생성 (만료 시간 외에는 별도 정보 없음)
        String refreshToken = Jwts.builder()
            .setExpiration(new Date(now + refreshTokenValidityInSeconds))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();

        return TokenResponse.builder()
            .grantType("Bearer")
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    /**
     * JWT 토큰을 복호화하여 토큰에 포함된 인증 정보를 반환하는 메서드
     * @param accessToken Access Token
     * @return Authentication 객체
     */
    public Authentication getAuthentication(String accessToken) {
        // 토큰에서 클레임(Claim) 추출
        Claims claims = parseClaims(accessToken);

        // ** 사용자 DB 조회 및 유효성 검증 로직 추가 **
        String loginId = claims.getSubject();
        com.yeoni.birdilegoapi.domain.entity.User user = userMapper.findByLoginId(loginId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!user.isEnabled()) {
            throw new CustomException(ErrorCode.USER_DISABLED);
        }


        if (claims.get("auth") == null) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        // 클레임에서 권한 정보 추출
        Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // UserDetails 객체를 생성하여 Authentication 객체로 반환
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    /**
     * 토큰의 유효성을 검증하는 메서드
     * @param token 검증할 JWT 토큰
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    /**
     * Access Token에서 클레임을 추출하는 private 메서드
     * @param accessToken Access Token
     * @return Claims 객체
     */
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            // 토큰이 만료되었더라도 클레임은 반환
            return e.getClaims();
        }
    }

}
