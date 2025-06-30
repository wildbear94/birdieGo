package com.yeoni.birdilegoapi.service;

import com.yeoni.birdilegoapi.domain.dto.group.GroupGenerationRequestDto;
import com.yeoni.birdilegoapi.domain.dto.group.LeagueType;
import com.yeoni.birdilegoapi.domain.entity.MatchGroup;
import com.yeoni.birdilegoapi.domain.entity.MatchGroupTeam;
import com.yeoni.birdilegoapi.domain.entity.ParticipantEntity;
import com.yeoni.birdilegoapi.mapper.MatchGroupMapper;
import com.yeoni.birdilegoapi.mapper.ParticipantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupGenerationService {

    private final ParticipantMapper participantMapper;
    private final MatchGroupMapper matchGroupMapper;

    @Transactional
    public void generateGroups(Long eventId, GroupGenerationRequestDto request) {
        // 1. 조건에 맞는 모든 참가자(팀) 목록을 한 번에 조회
        List<ParticipantEntity> participants = participantMapper.findParticipantsForGrouping(
            eventId, request.getEventType(), request.getAgeGroups(), request.getSkillLevels());

        if (participants == null || participants.isEmpty()) {
            throw new RuntimeException("조를 생성할 참가자가 없습니다.");
        }

        // 2. 전체 참가자 목록을 무작위로 섞음
        Collections.shuffle(participants);

        // 3. 리그 종류에 따라 조 편성
        if (request.getLeagueType() == LeagueType.FULL_LEAGUE) {
            generateGroupsByFixedSize(eventId, request, participants, 5);
        } else if (request.getLeagueType() == LeagueType.LEAGUE) {
            generateLeagueGroups(eventId, request, participants);
        } else if (request.getLeagueType() == LeagueType.TOURNAMENT) {
            // 토너먼트 로직 호출 (2팀씩 1조)
            generateGroupsByFixedSize(eventId, request, participants, 2);
        }
    }

    private void generateLeagueGroups(Long eventId, GroupGenerationRequestDto request, List<ParticipantEntity> participants) {
        int totalTeams = participants.size();
        int groupCount = 0;
        int currentIndex = 0;

        while(currentIndex < totalTeams) {
            int remainingTeams = totalTeams - currentIndex;
            int groupSize;

            if (remainingTeams >= 8 || remainingTeams == 6 || remainingTeams == 7) {
                groupSize = 4;
            } else groupSize = remainingTeams; // 5팀은 한 조로 편성

            // 최소 팀 수(3팀) 미만인 경우 조 생성 안함
            if (groupSize < 3 && totalTeams > 2) {
                // 마지막 남은 팀들을 앞 조에 분배하는 로직 추가 필요 (여기서는 생략)
                break;
            }

            List<ParticipantEntity> groupParticipants = participants.subList(currentIndex, currentIndex + groupSize);
            saveGroupAndTeams(eventId, request, ++groupCount, groupParticipants);
            currentIndex += groupSize;
        }
    }

    private void generateGroupsByFixedSize(Long eventId, GroupGenerationRequestDto request, List<ParticipantEntity> participants, int groupSize) {
        int totalTeams = participants.size();
        int groupCount = 0;
        for (int i = 0; i < totalTeams; i += 5) {
            int end = Math.min(i + 5, totalTeams);
            List<ParticipantEntity> groupParticipants = participants.subList(i, end);

            // 토너먼트가 아닌 경우, 마지막 조의 팀 수가 너무 적으면 편성하지 않음 (예외 처리)
            if (request.getLeagueType() != LeagueType.TOURNAMENT && groupParticipants.size() < 3 && totalTeams > groupSize) {
                // 남은 팀 처리 로직 (예: 앞 조에 편입)
                // 현재는 생략하고 중단
                break;
            }

            // 토너먼트의 경우, 마지막에 1팀이 남으면 부전승 처리 (편성하지 않음)
            if (request.getLeagueType() == LeagueType.TOURNAMENT && groupParticipants.size() < 2) {
                // 부전승 팀에 대한 별도 처리 로직 추가 가능
                break;
            }

            saveGroupAndTeams(eventId, request, ++groupCount, groupParticipants);
        }
    }

    private void saveGroupAndTeams(Long eventId, GroupGenerationRequestDto request, int groupNumber, List<ParticipantEntity> groupParticipants) {
        // 조 이름을 통합된 이름으로 생성
        String ageGroupStr = String.join("/", request.getAgeGroups());
        String skillLevelStr = String.join("/", request.getSkillLevels());
        String groupName;

        // 조 이름 생성 시 토너먼트 형식 분기 처리
        if (request.getLeagueType() == LeagueType.TOURNAMENT) {
            groupName = String.format("%s 통합(%s / %s) 토너먼트 %d경기", request.getEventType(), ageGroupStr, skillLevelStr, groupNumber);
        } else {
            groupName = String.format("%s 통합(%s / %s) %d조", request.getEventType(), ageGroupStr, skillLevelStr, groupNumber);
        }

        MatchGroup group = MatchGroup.builder()
            .eventId(eventId)
            .groupName(groupName)
            .eventType(request.getEventType())
            .ageGroup(ageGroupStr)
            .skillLevel(skillLevelStr)
            .leagueType(LeagueType.LEAGUE.name())
            .teamCount(groupParticipants.size())
            .build();
        matchGroupMapper.saveGroup(group); // 이 호출 후 group 객체에 groupId가 채워짐

        // b. 조-팀 매핑 정보 저장
        List<MatchGroupTeam> groupTeams = new ArrayList<>();
        for (ParticipantEntity p : groupParticipants) {
            groupTeams.add(MatchGroupTeam.builder()
                .groupId(group.getGroupId())
                .participantId(p.getParticipantId())
                .teamName(p.getTeamName())
                .build());
        }
        matchGroupMapper.saveGroupTeams(groupTeams);
    }

}
