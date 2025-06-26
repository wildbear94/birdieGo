package com.yeoni.birdilegoapi.domain.dto.auth;

import com.yeoni.birdilegoapi.domain.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private TokenResponse tokenInfo;
    private User userInfo;

}
