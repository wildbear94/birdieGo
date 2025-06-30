package com.yeoni.birdilegoapi.controller;

import com.yeoni.birdilegoapi.domain.dto.CommonResponse;
import com.yeoni.birdilegoapi.domain.dto.group.GroupGenerationRequestDto;
import com.yeoni.birdilegoapi.service.GroupGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/groups")
@RequiredArgsConstructor
public class GroupGenerationController {

    private final GroupGenerationService groupGenerationService;

    @PostMapping("/generate")
    public ResponseEntity<CommonResponse<Void>> generateGroups(
        @PathVariable Long eventId,
        @RequestBody GroupGenerationRequestDto request) {

        groupGenerationService.generateGroups(eventId, request);

        return ResponseEntity.ok(CommonResponse.<Void>builder()
            .status(HttpStatus.OK.value())
            .code("SUCCESS")
            .message("대진표 조 생성이 성공적으로 완료되었습니다.")
            .build());
    }

}
