package com.yeoni.birdilegoapi.controller;

import com.yeoni.birdilegoapi.domain.dto.CommonResponse;
import com.yeoni.birdilegoapi.domain.dto.group.GroupGenerationRequestDto;
import com.yeoni.birdilegoapi.domain.dto.group.MatchGroupDetailDto;
import com.yeoni.birdilegoapi.domain.entity.MatchGroup;
import com.yeoni.birdilegoapi.service.GroupGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<CommonResponse<List<MatchGroup>>> getGroupList(@PathVariable Long eventId) {
        List<MatchGroup> groups = groupGenerationService.getAllGroupsByEvent(eventId);
        return ResponseEntity.ok(CommonResponse.<List<MatchGroup>>builder()
            .status(HttpStatus.OK.value())
            .code("SUCCESS")
            .message("대진표 조 목록 조회가 성공적으로 완료되었습니다.")
            .data(groups)
            .build());
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<CommonResponse<MatchGroupDetailDto>> getGroupDetails(
        @PathVariable Long eventId,
        @PathVariable Long groupId) {
        MatchGroupDetailDto groupDetails = groupGenerationService.getGroupDetails(groupId);
        return ResponseEntity.ok(CommonResponse.<MatchGroupDetailDto>builder()
            .status(HttpStatus.OK.value())
            .code("SUCCESS")
            .message("대진표 조 상세 정보 조회가 성공적으로 완료되었습니다.")
            .data(groupDetails)
            .build());
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity<CommonResponse<Void>> deleteGroup(
        @PathVariable Long eventId,
        @PathVariable Long groupId) {
        groupGenerationService.deleteGroup(groupId);
        return ResponseEntity.ok(CommonResponse.<Void>builder()
            .status(HttpStatus.OK.value())
            .code("SUCCESS")
            .message("대진표 조가 성공적으로 삭제되었습니다.")
            .build());
    }
}
