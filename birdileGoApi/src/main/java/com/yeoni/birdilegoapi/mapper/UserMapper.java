package com.yeoni.birdilegoapi.mapper;

import com.yeoni.birdilegoapi.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    // 로그인 ID로 사용자 정보 조회
    Optional<User> findByLoginId(String loginId);

    // 사용자 ID로 사용자 정보 조회
    Optional<User> findById(Long userId);

    // 모든 사용자 목록 조회
    List<User> findAll();

    // 사용자 정보 저장 (회원가입)
    void save(User user);

    // 사용자 정보 수정
    void update(User user);

    // 사용자 삭제
    void deleteById(Long userId);
}
