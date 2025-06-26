package com.yeoni.birdilegoapi.controller;

import com.opencsv.exceptions.CsvValidationException;
import com.yeoni.birdilegoapi.domain.dto.CommonResponse;
import com.yeoni.birdilegoapi.domain.entity.ParticipantEntity;
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
                .message("참가자 파일이 성공적으로 업로드되었습니다.")
                .build());
        } catch (IOException | CsvValidationException e) {
            return buildErrorResponse("파일 처리 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<Void>> registerSingleParticipant(
        @PathVariable Long eventId,
        @RequestBody ParticipantEntity participant,
        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long uploaderId = userMapper.findByLoginId(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found")).getUserId();

            ParticipantEntity savedParticipant = participantService.registerSingleParticipant(participant, eventId, uploaderId);

            return ResponseEntity.ok(CommonResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .code("SUCCESS")
                .message("참가자가 성공적으로 등록되었습니다.")
                .build());
        } catch (IllegalArgumentException e) {
            return buildErrorResponse("INVALID_INPUT" + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return buildErrorResponse("UNAUTHORIZED" + e.getMessage(), HttpStatus.UNAUTHORIZED );
        }
    }

    /*@GetMapping
    public ResponseEntity<CommonResponse<List<ParticipantEntity>>> getParticipants(@PathVariable Long eventId) {
        List<ParticipantEntity> participants = participantService.getParticipantsByEvent(eventId);
        return ResponseEntity.ok(CommonResponse.<List<ParticipantEntity>>builder()
            .status(HttpStatus.OK.value())
            .message("업로드된 참가자 목록 조회 성공")
            .data(participants)
            .build());
    }*/

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
        @PathVariable Long participantId,
        @RequestBody ParticipantEntity participantDetails) {
        // eventId는 URL 경로에 있지만 현재 로직에서는 직접 사용하지 않음.
        // 필요 시 권한 체크 등에 활용 가능.
        ParticipantEntity updatedParticipant = participantService.updateParticipant(participantId, participantDetails);
        return ResponseEntity.ok(CommonResponse.<ParticipantEntity>builder()
            .status(HttpStatus.OK.value())
            .message("참가자 정보가 성공적으로 수정되었습니다.")
            .data(updatedParticipant)
            .build());
    }

    @DeleteMapping("/{participantId}")
    public ResponseEntity<CommonResponse<Void>> deleteParticipant(@PathVariable Long participantId, @PathVariable String eventId) {
        participantService.deleteParticipant(participantId);
        return ResponseEntity.ok(CommonResponse.<Void>builder()
            .status(HttpStatus.OK.value())
            .message("참가자 정보가 성공적으로 삭제되었습니다.")
            .build());
    }

    private ResponseEntity<CommonResponse<Void>> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status).body(CommonResponse.<Void>builder()
            .status(status.value())
            .message(message)
            .build());
    }

}
