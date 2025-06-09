package com.yeoni.birdilegoapi.mapper;

import com.yeoni.birdilegoapi.domain.dto.auth.RefreshTokenResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface RefreshTokenMapper {

    void save(RefreshTokenResponse refreshToken);
    Optional<RefreshTokenResponse> findByToken(String token);
    void deleteByToken(String token);
    // 특정 사용자의 모든 리프레시 토큰을 삭제할 때 사용 (선택적)
    void deleteByUserId(Long userId);

}
