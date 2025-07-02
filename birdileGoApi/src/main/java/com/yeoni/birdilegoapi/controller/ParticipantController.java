package com.yeoni.birdilegoapi.controller;

import com.opencsv.exceptions.CsvValidationException;
import com.yeoni.birdilegoapi.domain.dto.CommonResponse;
import com.yeoni.birdilegoapi.domain.dto.participant.ParticipantStats;
import com.yeoni.birdilegoapi.domain.dto.participant.ParticipantStatsByGroup;
import com.yeoni.birdilegoapi.domain.entity.ParticipantEntity;
import com.yeoni.birdilegoapi.exception.CustomException;
import com.yeoni.birdilegoapi.exception.ErrorCode;
import com.yeoni.birdilegoapi.mapper.UserMapper;
import com.yeoni.birdilegoapi.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events/{eventId}/participants")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<CommonResponse<Void>> uploadFile(
        @PathVariable Long eventId,
        @RequestParam("file") MultipartFile file,
        @AuthenticationPrincipal UserDetails userDetails) {

        if (file.isEmpty()) {
            return buildErrorResponse("업로드할 파일을 선택해주세요.", HttpStatus.BAD_REQUEST);
        }

        try {
            Long uploaderId = userMapper.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found")).getUserId();

            participantService.uploadParticipants(file, eventId, uploaderId);

            return ResponseEntity.ok(CommonResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .code("SUCCESS")
                .message("참가자 파일이 성공적으로 업로드되었습니다.")
                .build());
        } catch (IOException | CsvValidationException e) {
            return buildErrorResponse("파일 처리 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<ParticipantEntity>> registerSingleParticipant(
        @PathVariable Long eventId,
        @RequestBody ParticipantEntity participant,
        @AuthenticationPrincipal UserDetails userDetails) {
        // 요청을 보낸 사용자가 유효한지 확인
        userMapper.findByLoginId(userDetails.getUsername())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ParticipantEntity created = participantService.registerParticipant(eventId, participant);

        return ResponseEntity.status(HttpStatus.CREATED) // 생성 성공 시 201 Created 반환
            .body(CommonResponse.<ParticipantEntity>builder()
                .status(HttpStatus.CREATED.value())
                .code("SUCCESS")
                .message("참가자가 성공적으로 등록되었습니다.")
                .data(created)
                .build());
    }

    /**
     * 여러 참가자 정보를 JSON 배열로 받아 일괄 등록하는 API
     */
    @PostMapping("/register-multiple")
    public ResponseEntity<CommonResponse<Void>> registerMultipleParticipants(
        @PathVariable Long eventId,
        @RequestBody List<ParticipantEntity> participants,
        @AuthenticationPrincipal UserDetails userDetails) {

        try {
            Long uploaderId = userMapper.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)).getUserId();

            participantService.registerMultipleParticipants(participants, eventId, uploaderId);

            return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.<Void>builder()
                .status(HttpStatus.CREATED.value())
                .code("SUCCESS")
                .message("여러 참가자가 성공적으로 등록되었습니다.")
                .build());
        } catch (IllegalArgumentException e) {
            return this.<Void>buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, "INVALID_INPUT");
        }
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<ParticipantEntity>>> getParticipants(
        @PathVariable Long eventId,
        @RequestParam(required = false) String eventType) { // eventType 파라미터 추가

        List<ParticipantEntity> participants;

        if (eventType != null && !eventType.trim().isEmpty()) {
            // eventType 파라미터가 있으면 조건 검색
            participants = participantService.getParticipantsByEventAndType(eventId, eventType);
        } else {
            // eventType 파라미터가 없으면 전체 조회
            participants = participantService.getParticipantsByEvent(eventId);
        }

        return ResponseEntity.ok(CommonResponse.<List<ParticipantEntity>>builder()
            .status(HttpStatus.OK.value())
            .code("SUCCESS")
            .message("업로드된 참가자 목록 조회 성공")
            .data(participants)
            .build());
    }


    @PutMapping("/{participantId}")
    public ResponseEntity<CommonResponse<ParticipantEntity>> updateParticipant(
        @PathVariable Long eventId,
        @PathVariable Long participantId,
        @RequestBody ParticipantEntity participantDetails) {
        // eventId는 URL 경로에 있지만 현재 로직에서는 직접 사용하지 않음.
        // 필요 시 권한 체크 등에 활용 가능.
        ParticipantEntity updatedParticipant = participantService.updateParticipant(participantId, participantDetails);
        return ResponseEntity.ok(CommonResponse.<ParticipantEntity>builder()
            .status(HttpStatus.OK.value())
            .code("SUCCESS")
            .message("참가자 정보가 성공적으로 수정되었습니다.")
            .data(updatedParticipant)
            .build());
    }

    /**
     * 선택된 여러 참가자 정보를 일괄 수정하는 API
     */
    @PutMapping
    public ResponseEntity<CommonResponse<Void>> updateMultipleParticipants(
        @PathVariable Long eventId,
        @RequestBody List<ParticipantEntity> participants) {

        try {
            participantService.updateMultipleParticipants(participants);
            return ResponseEntity.ok(CommonResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .code("SUCCESS")
                .message("선택된 참가자 정보가 성공적으로 수정되었습니다.")
                .build());
        } catch (IllegalArgumentException e) {
            return this.<Void>buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, "INVALID_INPUT");
        }
    }

    @DeleteMapping("/{participantId}")
    public ResponseEntity<CommonResponse<Void>> deleteParticipant(@PathVariable Long participantId, @PathVariable String eventId) {
        participantService.deleteParticipant(participantId);
        return ResponseEntity.ok(CommonResponse.<Void>builder()
            .status(HttpStatus.OK.value())
            .code("SUCCESS")
            .message("참가자 정보가 성공적으로 삭제되었습니다.")
            .build());
    }

    /**
     * 특정 대회의 모든 참가자를 삭제하는 API
     */
    @DeleteMapping("/all")
    public ResponseEntity<CommonResponse<Void>> deleteAllParticipants(@PathVariable Long eventId) {
        participantService.deleteAllParticipantsByEvent(eventId);
        return ResponseEntity.ok(CommonResponse.<Void>builder()
            .status(HttpStatus.OK.value())
            .code("SUCCESS")
            .message("해당 대회의 모든 참가자가 성공적으로 삭제되었습니다.")
            .build());
    }

    /**
     * 선택된 여러 참가자를 일괄 삭제하는 API
     */
    @DeleteMapping
    public ResponseEntity<CommonResponse<Void>> deleteMultipleParticipants(
        @PathVariable Long eventId,
        @RequestBody List<Long> uploadIds) {
        // eventId는 특정 대회의 컨텍스트를 유지하기 위해 경로에 포함, 실제 로직엔 불필요
        try {
            participantService.deleteMultipleParticipants(uploadIds);
            return ResponseEntity.ok(CommonResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .code("SUCCESS")
                .message("선택된 참가자가 성공적으로 삭제되었습니다.")
                .build());
        } catch (IllegalArgumentException e) {
            // buildErrorResponse 메서드 재활용
            return this.<Void>buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, "INVALID_INPUT");
        }
    }

    /**
     * 참가자 통계를 조회하는 API
     */
    @GetMapping("/stats")
    public ResponseEntity<CommonResponse<List<ParticipantStats>>> getParticipantStatistics(
        @PathVariable Long eventId,
        @RequestParam String eventType) {

        try {
            List<ParticipantStats> stats = participantService.getParticipantStats(eventId, eventType);
            return ResponseEntity.ok(CommonResponse.<List<ParticipantStats>>builder()
                .status(HttpStatus.OK.value())
                .code("SUCCESS")
                .message("참가자 통계 조회가 성공적으로 완료되었습니다.")
                .data(stats)
                .build());
        } catch (IllegalArgumentException e) {
            // buildErrorResponse 메서드를 재활용하기 위해 제네릭 타입을 <List<ParticipantStatsDto>>로 명시
            return this.<List<ParticipantStats>>buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, "INVALID_INPUT");
        }
    }


    /**
     * 전체 참가자 통계를 중첩된 JSON 구조로 조회하는 API
     */
    @GetMapping("/stats/all")
    public ResponseEntity<CommonResponse<Map<String, List<ParticipantStatsByGroup>>>> getAllParticipantStatistics(
        @PathVariable Long eventId) {

        Map<String, List<ParticipantStatsByGroup>> statsMap = participantService.getParticipantStatsGroupedByEventType(eventId);

        return ResponseEntity.ok(CommonResponse.<Map<String, List<ParticipantStatsByGroup>>>builder()
            .status(HttpStatus.OK.value())
            .code("SUCCESS")
            .message("전체 참가자 통계 조회가 성공적으로 완료되었습니다.")
            .data(statsMap)
            .build());
    }

    private ResponseEntity<CommonResponse<Void>> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(CommonResponse.<Void>builder()
            .status(status.value())
            .message(message)
            .build());
    }

    private <T> ResponseEntity<CommonResponse<T>> buildErrorResponse(String message, HttpStatus status, String code) {
        return ResponseEntity.status(status).body(CommonResponse.<T>builder()
            .status(status.value())
            .code(code)
            .message(message)
            .build());
    }

}
