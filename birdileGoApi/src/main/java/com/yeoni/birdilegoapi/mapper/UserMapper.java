package com.yeoni.birdilegoapi.mapper;

import com.yeoni.birdilegoapi.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface UserMapper {
    // 로그인 ID로 사용자 정보 조회
    Optional<User> findByLoginId(String loginId);

    // 사용자 정보 저장 (회원가입)
    void save(User user);
}
