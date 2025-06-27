package com.yeoni.birdilegoapi.config;

import com.yeoni.birdilegoapi.common.CustomAuthenticationEntryPoint;
import com.yeoni.birdilegoapi.filter.JwtAuthenticationFilter;
import com.yeoni.birdilegoapi.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint; // 의존성 주입
    private static final String[] SWAGGER_WHITELIST = {
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/v3/api-docs"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF, Form Login, HTTP Basic 비활성화
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)

            // 세션을 사용하지 않음 (Stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))


            // API 경로별 접근 권한 설정
            .authorizeHttpRequests(auth -> auth
                // Swagger UI 및 API 문서 허용
                .requestMatchers(SWAGGER_WHITELIST).permitAll()
                .requestMatchers("/api/auth","/api/auth/**").permitAll() // 로그인은 모두 허용
                .requestMatchers("/api/events", "/api/events/**").permitAll() // 대회 조회는 모두 허용
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/users/register").permitAll()
                .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
            )

            // 예외 처리 설정 추가
            //.exceptionHandling(ex -> ex.authenticationEntryPoint(customAuthenticationEntryPoint))

            // 직접 만든 JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
