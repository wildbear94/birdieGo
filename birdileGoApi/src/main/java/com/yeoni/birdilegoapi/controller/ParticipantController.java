package com.yeoni.birdilegoapi.controller;

import com.yeoni.birdilegoapi.domain.entity.ParticipantEntity;
import com.yeoni.birdilegoapi.mapper.UserMapper;
import com.yeoni.birdilegoapi.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/participants")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<ParticipantEntity> registerParticipant(@PathVariable Long eventId, @RequestBody ParticipantEntity participant, @AuthenticationPrincipal UserDetails userDetails) {
        // 현재 사용자가 존재하는지만 확인
        userMapper.findByLoginId(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("User not found"));
        ParticipantEntity created = participantService.registerParticipant(eventId, participant);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<ParticipantEntity>> listParticipants(@PathVariable Long eventId) {
        return ResponseEntity.ok(participantService.getParticipantsByEvent(eventId));
    }

    @PutMapping("/{participantId}")
    public ResponseEntity<ParticipantEntity> updateParticipant(@PathVariable Long eventId,
                                                               @PathVariable Long participantId,
                                                               @RequestBody ParticipantEntity participantDetails) {
        ParticipantEntity updated = participantService.updateParticipant(participantId, participantDetails);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{participantId}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable Long eventId,
                                                  @PathVariable Long participantId) {
        participantService.deleteParticipant(eventId, participantId);
        return ResponseEntity.noContent().build();
    }

}
