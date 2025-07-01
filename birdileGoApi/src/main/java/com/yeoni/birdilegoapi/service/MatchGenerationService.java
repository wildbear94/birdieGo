package com.yeoni.birdilegoapi.service;

import com.yeoni.birdilegoapi.domain.dto.group.MatchGroupDetailDto;
import com.yeoni.birdilegoapi.domain.dto.match.MatchGenerationRequestDto;
import com.yeoni.birdilegoapi.domain.entity.Match;
import com.yeoni.birdilegoapi.domain.entity.MatchGroup;
import com.yeoni.birdilegoapi.domain.entity.MatchGroupTeam;
import com.yeoni.birdilegoapi.exception.CustomException;
import com.yeoni.birdilegoapi.exception.ErrorCode;
import com.yeoni.birdilegoapi.mapper.MatchGroupMapper;
import com.yeoni.birdilegoapi.mapper.MatchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MatchGenerationService {

    private final MatchGroupMapper matchGroupMapper;
    private final MatchMapper matchMapper;

    @Transactional
    public void generateLeagueMatches(Long eventId, MatchGenerationRequestDto request) {
        // 1. 해당 대회의 리그/풀리그 조 목록을 가져옴
        List<MatchGroup> groups =
            matchGroupMapper.findGroupsByEventTypeAndLeagueTypes(eventId, request.getEventType(), List.of("LEAGUE", "FULL_LEAGUE"));

        List<Match> allMatches = new ArrayList<>();

        // 2. 각 조별로 라운드 로빈 대진 생성
        for (com.yeoni.birdilegoapi.domain.entity.MatchGroup group : groups) {
            MatchGroupDetailDto groupDetails = matchGroupMapper.findGroupDetailsById(group.getGroupId()).orElse(null);
            if (groupDetails == null || groupDetails.getTeams() == null || groupDetails.getTeams().size() < 2) continue;

            List<MatchGroupTeam> teams = groupDetails.getTeams();
            for (int i = 0; i < teams.size(); i++) {
                for (int j = i + 1; j < teams.size(); j++) {
                    MatchGroupTeam team1 = teams.get(i);
                    MatchGroupTeam team2 = teams.get(j);

                    allMatches.add(Match.builder()
                        .eventId(eventId)
                        .groupId(group.getGroupId())
                        .groupName(group.getGroupName())
                        .team1Id(team1.getParticipantId())
                        .team1Name(team1.getTeamName())
                        .team2Id(team2.getParticipantId())
                        .team2Name(team2.getTeamName())
                        .build());
                }
            }
        }

        if (allMatches.isEmpty()) {
            throw new RuntimeException("생성할 경기가 없습니다.");
        }

        // 3. 생성된 모든 경기를 코트에 배정
        scheduleMatches(allMatches, request);

        // 4. DB에 경기 정보 저장
        matchMapper.saveMatches(allMatches);
    }

    private void scheduleMatches(List<Match> matches, MatchGenerationRequestDto request) {
        // 각 코트별 다음 경기 가능 시간을 저장하는 맵
        Map<Integer, LocalDateTime> courtAvailability = new HashMap<>();
        for (int i = 1; i <= request.getCourtCount(); i++) {
            courtAvailability.put(i, request.getStartTime());
        }

        for (Match match : matches) {
            int bestCourt = -1;
            LocalDateTime earliestTime = null;

            // 가장 빨리 경기를 시작할 수 있는 코트를 찾음
            for (Map.Entry<Integer, LocalDateTime> entry : courtAvailability.entrySet()) {
                if (earliestTime == null || entry.getValue().isBefore(earliestTime)) {
                    earliestTime = entry.getValue();
                    bestCourt = entry.getKey();
                }
            }

            // 선택된 코트에 경기 배정
            match.setCourt(bestCourt + "번 코트");
            match.setMatchTime(earliestTime);

            // 해당 코트의 다음 경기 가능 시간 업데이트 (경기 시간 + 휴식 시간)
            LocalDateTime nextAvailableTime = earliestTime
                .plusMinutes(request.getMatchDurationMinutes())
                .plusMinutes(request.getRestTimeMinutes());
            courtAvailability.put(bestCourt, nextAvailableTime);
        }
    }

    public List<Match> getMatchesByEvent(Long eventId) {
        return matchMapper.findAllByEventId(eventId);
    }

    public Match getMatchById(Long matchId) {
        return matchMapper.findById(matchId)
            .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND)); // 적절한 에러코드로 변경
    }

    @Transactional
    public Match updateMatch(Long matchId, Match matchDetails) {
        // 수정 전, 대상 경기 존재 여부 확인
        Match existingMatch = matchMapper.findById(matchId)
            .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND)); // 적절한 에러코드로 변경

        // DTO의 필드 값으로 기존 엔티티 업데이트 (일부 필드만 업데이트 가능하도록)
        if (matchDetails.getCourt() != null) {
            existingMatch.setCourt(matchDetails.getCourt());
        }
        if (matchDetails.getMatchTime() != null) {
            existingMatch.setMatchTime(matchDetails.getMatchTime());
        }
        if (matchDetails.getMatchStatus() != null) {
            existingMatch.setMatchStatus(matchDetails.getMatchStatus());
        }

        matchMapper.update(existingMatch);
        return existingMatch;
    }

    @Transactional
    public void deleteMatch(Long matchId) {
        // 삭제 전, 대상 경기 존재 여부 확인
        matchMapper.findById(matchId)
            .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND)); // 적절한 에러코드로 변경
        matchMapper.deleteById(matchId);
    }

}
