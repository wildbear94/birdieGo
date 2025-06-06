package com.yeoni.birdilegoapi.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    // HTTP 요청 헤더의 "Authorization" 에서 Bearer 토큰을 추출하기 위한 상수
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "Bearer";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. Request Header에서 JWT 토큰 추출
        String token = resolveToken(request);

        // 2. validateToken 으로 토큰 유효성 검사
        // 토큰이 존재하고 유효하다면
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // 3. 토큰이 유효할 경우 토큰에서 Authentication 객체를 받아옴
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            // 4. SecurityContext에 Authentication 객체를 저장
            // 이 시점부터 해당 요청을 처리하는 동안 사용자는 인증된 것으로 간주됨
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), request.getRequestURI());
        } else {
            log.debug("유효한 JWT 토큰이 없습니다, uri: {}", request.getRequestURI());
        }

        // 5. 다음 필터로 제어를 넘김
        // 필터 체인의 다음 필터로 요청과 응답을 전달하여 계속 처리하도록 함
        filterChain.doFilter(request, response);
    }

    /**
     * Request Header에서 토큰 정보를 추출하는 메서드
     * @param request 클라이언트의 HttpServletRequest
     * @return 추출된 토큰 문자열, 없으면 null
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        // 헤더가 존재하고 "Bearer "로 시작하는지 확인
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            // "Bearer " 접두사를 제외한 실제 토큰 부분만 반환
            return bearerToken.substring(7);
        }
        return null;
    }


}
