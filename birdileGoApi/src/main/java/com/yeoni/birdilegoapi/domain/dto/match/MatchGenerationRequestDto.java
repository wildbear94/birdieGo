package com.yeoni.birdilegoapi.domain.dto.match;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class MatchGenerationRequestDto {

    private String eventType;
    private int courtCount; // 경기장 수

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime; // 최초 시작 시간 (예: "2025-07-20T09:00:00")

    private int matchDurationMinutes; // 경기 진행 시간 (분)
    private int restTimeMinutes;      // 경기 간 휴식 시간 (분)

}
