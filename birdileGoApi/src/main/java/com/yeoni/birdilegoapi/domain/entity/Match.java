package com.yeoni.birdilegoapi.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Match {
    private Long matchId;
    private Long eventId;
    private Long groupId;
    private String groupName;
    private String court; // 경기장 번호 (예: 1번 코트)
    private LocalDateTime matchTime; // 경기 시작 시간
    private String team1Name;
    private Long team1Id; // participant_upload_id
    private String team2Name;
    private Long team2Id; // participant_upload_id
    private String matchStatus;
    private String roundInfo;
}
