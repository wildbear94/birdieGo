package com.yeoni.birdilegoapi.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.yeoni.birdilegoapi.domain.dto.participant.ParticipantStats;
import com.yeoni.birdilegoapi.domain.dto.participant.ParticipantStatsByGroup;
import com.yeoni.birdilegoapi.domain.entity.ParticipantEntity;
import com.yeoni.birdilegoapi.mapper.ParticipantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantMapper participantMapper;

    @Transactional
    public void uploadParticipants(MultipartFile file, Long eventId, Long uploaderId) throws IOException, CsvValidationException {
        List<ParticipantEntity> participantList = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String[] nextLine;
            reader.readNext(); // 헤더 라인 스킵

            while ((nextLine = reader.readNext()) != null) {
                // CSV 파일의 각 컬럼을 매핑합니다.
                // CSV 파일에 2명의 참가자 정보가 있으므로, participant2 정보가 없을 경우를 대비하여 null 체크를 합니다.
                ParticipantEntity participant = ParticipantEntity.builder()
                    .eventId(eventId)
                    .uploaderId(uploaderId)
                    .eventType(nextLine[1])
                    .ageGroup(nextLine[2])
                    .skillLevel(nextLine[3])
                    .teamName(nextLine[4])
                    .participant1Name(nextLine[5])
                    .participant1Birth(nextLine[6])
                    .participant1Phone(nextLine[7])
                    .participant2Name(nextLine.length > 8 ? nextLine[8] : null)
                    .participant2Birth(nextLine.length > 9 ? nextLine[9] : null)
                    .participant2Phone(nextLine.length > 10 ? nextLine[10] : null)
                    .build();
                participantList.add(participant);
            }
        }
        if (!participantList.isEmpty()) {
            participantMapper.saveBatch(participantList);
        }
    }

    @Transactional
    public ParticipantEntity registerSingleParticipant(ParticipantEntity participant, Long eventId, Long uploaderId) {
        if (participant == null) {
            throw new IllegalArgumentException("참가자 정보가 비어있습니다.");
        }
        participant.setEventId(eventId);
        participant.setUploaderId(uploaderId);
        participantMapper.save(participant); // MyBatis는 insert 후 participant 객체에 생성된 ID를 자동으로 채워줍니다.
        return participant;
    }



    @Transactional
    public ParticipantEntity registerParticipant(Long eventId, ParticipantEntity participant) {
        participant.setEventId(eventId);
        participantMapper.save(participant);
        return participant;
    }

    public Optional<ParticipantEntity> getParticipantById(Long participantId) {
        return participantMapper.findById(participantId);
    }

    public List<ParticipantEntity> getParticipantsByEvent(Long eventId) {
        return participantMapper.findByEventId(eventId);
    }

    /**
     * eventType으로 참가자 목록을 검색하는 메서드
     */
    public List<ParticipantEntity> getParticipantsByEventAndType(Long eventId, String eventType) {
        return participantMapper.findByEventIdAndEventType(eventId, eventType);
    }

    @Transactional
    public ParticipantEntity updateParticipant(Long participantId, ParticipantEntity participantDetails) {
        participantDetails.setParticipantId(participantId);
        participantMapper.update(participantDetails);
        return participantMapper.findById(participantId)
            .orElseThrow(() -> new RuntimeException("Participant not found after update"));
    }

    @Transactional
    public void deleteParticipant(Long participantId) {
        participantMapper.deleteById(participantId);
    }

    /**
     * 참가자 통계를 조회하는 서비스 메서드
     */
    public List<ParticipantStats> getParticipantStats(Long eventId, String eventType) {
        if (eventType == null || eventType.trim().isEmpty()) {
            throw new IllegalArgumentException("종목(eventType)은 필수 입력값입니다.");
        }
        return participantMapper.getStatsByEventType(eventId, eventType);
    }

    /**
     * 전체 참가자 통계를 조회하는 서비스 메서드
     */
    public List<ParticipantStats> getParticipantStatsForAllEventTypes(Long eventId) {
        return participantMapper.getStatsForAllEventTypes(eventId);
    }

    /**
        * 전체 통계 결과를 eventType을 key로 하는 Map으로 가공하는 메서드
     */
    public Map<String, List<ParticipantStatsByGroup>> getParticipantStatsGroupedByEventType(Long eventId) {
        // 1. DB에서 플랫한 통계 리스트를 조회합니다.
        List<ParticipantStats> flatList = participantMapper.getStatsForAllEventTypes(eventId);

        // 2. Stream API의 Collectors.groupingBy를 사용하여 Map으로 변환합니다.
        return flatList.stream()
            .collect(Collectors.groupingBy(
                ParticipantStats::getEventType, // 최상위 키가 될 eventType을 지정
                Collectors.mapping( // 값(value)이 될 리스트의 요소를 변환
                    stats -> new ParticipantStatsByGroup(
                        stats.getAgeGroup(),
                        stats.getSkillLevel(),
                        stats.getTeamCount()
                    ),
                    Collectors.toList() // 변환된 요소들을 리스트로 수집
                )
            ));
    }

}
