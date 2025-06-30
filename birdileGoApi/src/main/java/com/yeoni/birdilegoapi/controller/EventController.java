package com.yeoni.birdilegoapi.controller;

import com.yeoni.birdilegoapi.domain.dto.CommonResponse;
import com.yeoni.birdilegoapi.domain.dto.event.EventRequestDto;
import com.yeoni.birdilegoapi.domain.entity.EventEntity;
import com.yeoni.birdilegoapi.exception.CustomException;
import com.yeoni.birdilegoapi.exception.ErrorCode;
import com.yeoni.birdilegoapi.mapper.UserMapper;
import com.yeoni.birdilegoapi.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final UserMapper userMapper; // creatorId를 얻기 위해

    // 이벤트 생성
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CommonResponse<EventEntity>> createEvent(
        @RequestBody EventRequestDto requestDto,
        @RequestPart(value = "files", required = false) List<MultipartFile> files,
        @AuthenticationPrincipal UserDetails userDetails) {
        // Spring Security 컨텍스트에서 사용자 정보(loginId)를 가져옴
        String loginId = userDetails.getUsername();
        // loginId로 실제 User 객체를 조회하여 creatorId를 얻음
        Long creatorId = userMapper.findByLoginId(loginId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)).getUserId();

        EventEntity createdEventEntity = eventService.createEvent(requestDto, creatorId, files).getEventEntity();

        return ResponseEntity.status(HttpStatus.CREATED).body(
            CommonResponse.<EventEntity>builder()
                .status(HttpStatus.CREATED.value())
                .code("SUCCESS")
                .message("대회가 성공적으로 생성되었습니다.")
                .data(createdEventEntity)
                .build());
    }

    // 모든 이벤트 목록 조회
    @GetMapping
    public ResponseEntity<CommonResponse<List<EventEntity>>> getAllEvents(@AuthenticationPrincipal UserDetails userDetails) {
        // Spring Security 컨텍스트에서 사용자 정보(loginId)를 가져옴
        String loginId = userDetails.getUsername();
        // loginId로 실제 User 객체를 조회하여 creatorId를 얻음
        Long creatorId = userMapper.findByLoginId(loginId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)).getUserId();

        List<EventEntity> eventEntities = eventService.getAllEvents(creatorId);

        return ResponseEntity.ok(
            CommonResponse.<List<EventEntity>>builder()
                .status(HttpStatus.OK.value())
                .code("SUCCESS")
                .message("대회 목록 조회가 성공적으로 완료되었습니다.")
                .data(eventEntities)
                .build());
    }

    // 특정 이벤트 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<EventEntity>> getEventById(@PathVariable Long id) {
        EventEntity event = eventService.getEventById(id)
            .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        return ResponseEntity.ok(
            CommonResponse.<EventEntity>builder()
                .status(HttpStatus.OK.value())
                .code("SUCCESS")
                .message("대회 상세 정보 조회가 성공적으로 완료되었습니다.")
                .data(event)
                .build());
    }

    // 이벤트 수정
    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<EventEntity>> updateEvent(@PathVariable Long id, @RequestBody EventEntity eventEntityDetails, @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = userMapper.findByLoginId(userDetails.getUsername())
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)).getUserId();

        EventEntity updatedEvent = eventService.updateEvent(id, eventEntityDetails, currentUserId);

        return ResponseEntity.ok(
            CommonResponse.<EventEntity>builder()
                .status(HttpStatus.OK.value())
                .code("SUCCESS")
                .message("대회 정보가 성공적으로 수정되었습니다.")
                .data(updatedEvent)
                .build());
    }

    // 이벤트 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> deleteEvent(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = userMapper.findByLoginId(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found")).getUserId();

        eventService.deleteEvent(id, currentUserId);

        return ResponseEntity.ok(
            CommonResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .code("SUCCESS")
                .message("대회 정보가 성공적으로 삭제되었습니다.")
                .build());
    }
}
