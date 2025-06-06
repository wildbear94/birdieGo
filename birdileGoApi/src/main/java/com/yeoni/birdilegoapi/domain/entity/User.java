package com.yeoni.birdilegoapi.domain.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class User {
    private Long userId;
    private String loginId;
    private String displayName;
    private String password;
    private boolean enabled;
    private String contactPhone;
    private String email;
    private String companyName;
    private String position;
    private String jobLevel;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
