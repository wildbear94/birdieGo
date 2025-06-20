package com.yeoni.birdilegoapi.controller;

import com.yeoni.birdilegoapi.domain.dto.event.EventRequestDto;
import com.yeoni.birdilegoapi.domain.entity.EventEntity;
import com.yeoni.birdilegoapi.mapper.UserMapper;
import com.yeoni.birdilegoapi.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final UserMapper userMapper; // creatorId를 얻기 위해

    // 이벤트 생성
    @PostMapping
    public ResponseEntity<EventEntity> createEvent(@RequestBody EventRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {
        // Spring Security 컨텍스트에서 사용자 정보(loginId)를 가져옴
        String loginId = userDetails.getUsername();
        // loginId로 실제 User 객체를 조회하여 creatorId를 얻음
        Long creatorId = userMapper.findByLoginId(loginId)
            .orElseThrow(() -> new RuntimeException("User not found")).getUserId();

        EventEntity createdEventEntity = eventService.createEvent(requestDto, creatorId).getEventEntity();
        return ResponseEntity.ok(createdEventEntity);
    }

    // 모든 이벤트 목록 조회
    @GetMapping
    public ResponseEntity<List<EventEntity>> getAllEvents(@AuthenticationPrincipal UserDetails userDetails) {
        // Spring Security 컨텍스트에서 사용자 정보(loginId)를 가져옴
        String loginId = userDetails.getUsername();
        // loginId로 실제 User 객체를 조회하여 creatorId를 얻음
        Long creatorId = userMapper.findByLoginId(loginId)
            .orElseThrow(() -> new RuntimeException("User not found")).getUserId();

        List<EventEntity> eventEntities = eventService.getAllEvents(creatorId);
        return ResponseEntity.ok(eventEntities);
    }

    // 특정 이벤트 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<EventEntity> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // 이벤트 수정
    @PutMapping("/{id}")
    public ResponseEntity<EventEntity> updateEvent(@PathVariable Long id, @RequestBody EventEntity eventEntityDetails, @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = userMapper.findByLoginId(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found")).getUserId();
        try {
            EventEntity updatedEventEntity = eventService.updateEvent(id, eventEntityDetails, currentUserId);
            return ResponseEntity.ok(updatedEventEntity);
        } catch (RuntimeException e) {
            // 권한 없음 또는 리소스 없음 등의 예외 처리
            return ResponseEntity.badRequest().body(null); // 실제로는 GlobalExceptionHandler에서 처리하는 것이 좋음
        }
    }

    // 이벤트 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Long currentUserId = userMapper.findByLoginId(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found")).getUserId();
        try {
            eventService.deleteEvent(id, currentUserId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
