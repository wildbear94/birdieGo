package com.yeoni.birdilegoapi.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.yeoni.birdilegoapi.domain.dto.participant.ParticipantStats;
import com.yeoni.birdilegoapi.domain.dto.participant.ParticipantStatsByGroup;
import com.yeoni.birdilegoapi.domain.entity.ParticipantEntity;
import com.yeoni.birdilegoapi.exception.CustomException;
import com.yeoni.birdilegoapi.exception.ErrorCode;
import com.yeoni.birdilegoapi.mapper.ParticipantMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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
                    .eventType(nextLine[1].trim())
                    .ageGroup(nextLine[2].trim())
                    .skillLevel(nextLine[3].trim())
                    .teamName(nextLine[4].trim())
                    .participant1Name(nextLine[5].trim())
                    .participant1Birth(nextLine[6].trim())
                    .participant1Phone(nextLine[7].trim())
                    .participant2Name(nextLine.length > 8 ? nextLine[8].trim() : null)
                    .participant2Birth(nextLine.length > 9 ? nextLine[9].trim() : null)
                    .participant2Phone(nextLine.length > 10 ? nextLine[10].trim() : null)
                    .build();
                participantList.add(participant);
            }
        }
        if (!participantList.isEmpty()) {
            participantMapper.saveBatch(participantList);
        }
    }

    @Transactional
    public ParticipantEntity registerParticipant(Long eventId, ParticipantEntity participant) {
        if (participant == null) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        participant.setEventId(eventId);
        participantMapper.save(participant);
        return participant;
    }

    /**
     * 여러 명의 참가자 정보를 JSON 배열로 받아 일괄 등록하는 서비스 메서드
     */
    @Transactional
    public void registerMultipleParticipants(List<ParticipantEntity> participants, Long eventId, Long uploaderId) {
        if (CollectionUtils.isEmpty(participants)) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        // 각 참가자 객체에 eventId와 uploaderId를 설정
        for (ParticipantEntity participant : participants) {
            participant.setEventId(eventId);
            participant.setUploaderId(uploaderId);
            // TODO: 각 참가자 정보에 대한 유효성 검증 로직 추가 가능
        }

        participantMapper.saveBatch(participants);
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
            .orElseThrow(() -> new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    /**
     * 여러 참가자 정보를 일괄 수정하는 서비스 메서드
     */
    @Transactional
    public void updateMultipleParticipants(List<ParticipantEntity> participants) {
        if (participants == null || participants.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }
        // TODO: 수정 전, 각 참가자가 DB에 실제로 존재하는지 확인하는 검증 로직 추가 가능
        participantMapper.updateMultiple(participants);
    }

    @Transactional
    public void deleteParticipant(Long participantId) {

        participantMapper.findById(participantId)
            .orElseThrow(() -> new CustomException(ErrorCode.PARTICIPANT_NOT_FOUND));

        participantMapper.deleteById(participantId);
    }

    /**
     * 특정 대회의 모든 참가자를 삭제하는 서비스 메서드
     */
    @Transactional
    public void deleteAllParticipantsByEvent(Long eventId) {
        // TODO: 권한 체크 로직 추가 가능 (예: 대회 생성자만 삭제 가능)
        participantMapper.deleteAllByEventId(eventId);
    }

    /**
     * 여러 참가자를 일괄 삭제하는 서비스 메서드
     */
    @Transactional
    public void deleteMultipleParticipants(List<Long> uploadIds) {
        if (uploadIds == null || uploadIds.isEmpty()) {
            throw new CustomException(ErrorCode.PARTICIPANT_NOT_FOUND);
        }
        // TODO: 권한 체크 로직 추가 가능
        participantMapper.deleteByIds(uploadIds);
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
    public Map<String, List<ParticipantStatsByGroup>> getParticipantStatsGroupedByEventType(Long eventId){
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
