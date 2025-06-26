package com.yeoni.birdilegoapi.service;

import com.yeoni.birdilegoapi.domain.entity.User;
import com.yeoni.birdilegoapi.exception.CustomException;
import com.yeoni.birdilegoapi.exception.ErrorCode;
import com.yeoni.birdilegoapi.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // loginId로 사용자 정보를 DB에서 조회합니다.
        return userMapper.findByLoginId(username)
            .map(this::createUserDetails)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    // DB에서 조회한 User 엔티티를 Spring Security가 사용하는 UserDetails 객체로 변환합니다.
    private UserDetails createUserDetails(User user) {
        if (!user.isEnabled()) {
            throw new CustomException(ErrorCode.USER_DISABLED);
        }
        // 현재 권한(Role) 체계가 없으므로 빈 권한 목록을 전달합니다.
        // 추후 권한 체계가 생기면 user.getRoles() 같은 형태로 실제 권한을 부여해야 합니다.
        return new org.springframework.security.core.userdetails.User(
            user.getLoginId(),
            user.getPassword(), // DB에 저장된 암호화된 비밀번호
            Collections.emptyList() // 권한 목록
        );
    }

}
