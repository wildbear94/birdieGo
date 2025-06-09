package com.yeoni.birdilegoapi.domain.dto.auth;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class RefreshTokenResponse {

    private Long tokenId;
    private Long userId;
    private String token;
    private Timestamp expiresAt;

}
