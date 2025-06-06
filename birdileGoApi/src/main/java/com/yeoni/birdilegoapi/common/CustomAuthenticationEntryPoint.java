package com.yeoni.birdilegoapi.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoni.birdilegoapi.domain.dto.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint{

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.error("Authentication error for a protected resource: {}", authException.getMessage());

        // 응답 상태 코드 설정 (401 Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // 응답 컨텐츠 타입을 JSON으로 설정
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // 문자 인코딩을 UTF-8로 설정
        response.setCharacterEncoding("UTF-8");

        // 커스텀 에러 응답 DTO 생성
        ErrorResponse errorResponse = ErrorResponse.builder()
            .status(HttpServletResponse.SC_UNAUTHORIZED)
            .code("INVALID_TOKEN")
            .message("인증이 필요합니다. 유효하지 않거나 만료된 토큰입니다.")
            .build();

        // DTO를 JSON 문자열로 변환하여 응답 본문에 작성
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}
