package com.yeoni.birdilegoapi.controller;

import com.yeoni.birdilegoapi.domain.dto.CommonResponse;
import com.yeoni.birdilegoapi.domain.dto.match.MatchGenerationRequestDto;
import com.yeoni.birdilegoapi.domain.entity.Match;
import com.yeoni.birdilegoapi.service.MatchGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events/{eventId}/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchGenerationService matchGenerationService;

    @PostMapping("/generate-league")
    public ResponseEntity<CommonResponse<Void>> generateLeagueMatches(
        @PathVariable Long eventId,
        @RequestBody MatchGenerationRequestDto request) {

        matchGenerationService.generateLeagueMatches(eventId, request);

        return ResponseEntity.ok(CommonResponse.<Void>builder()
            .status(HttpStatus.OK.value())
            .code("SUCCESS")
            .message("리그 경기 생성이 성공적으로 완료되었습니다.")
            .build());
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<Match>>> getMatchList(@PathVariable Long eventId) {
        List<Match> matches = matchGenerationService.getMatchesByEvent(eventId);
        return ResponseEntity.ok(CommonResponse.<List<Match>>builder()
            .status(HttpStatus.OK.value()).code("SUCCESS")
            .message("경기 목록 조회가 성공적으로 완료되었습니다.")
            .data(matches).build());
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<CommonResponse<Match>> getMatchDetails(
        @PathVariable Long eventId,
        @PathVariable Long matchId) {
        Match match = matchGenerationService.getMatchById(matchId);
        return ResponseEntity.ok(CommonResponse.<Match>builder()
            .status(HttpStatus.OK.value()).code("SUCCESS")
            .message("경기 상세 정보 조회가 성공적으로 완료되었습니다.")
            .data(match).build());
    }

    @PutMapping("/{matchId}")
    public ResponseEntity<CommonResponse<Match>> updateMatch(
        @PathVariable Long eventId,
        @PathVariable Long matchId,
        @RequestBody Match matchDetails) {
        Match updatedMatch = matchGenerationService.updateMatch(matchId, matchDetails);
        return ResponseEntity.ok(CommonResponse.<Match>builder()
            .status(HttpStatus.OK.value()).code("SUCCESS")
            .message("경기 정보가 성공적으로 수정되었습니다.")
            .data(updatedMatch).build());
    }

    @DeleteMapping("/{matchId}")
    public ResponseEntity<CommonResponse<Void>> deleteMatch(
        @PathVariable Long eventId,
        @PathVariable Long matchId) {
        matchGenerationService.deleteMatch(matchId);
        return ResponseEntity.ok(CommonResponse.<Void>builder()
            .status(HttpStatus.OK.value()).code("SUCCESS")
            .message("경기가 성공적으로 삭제되었습니다.").build());
    }

}
