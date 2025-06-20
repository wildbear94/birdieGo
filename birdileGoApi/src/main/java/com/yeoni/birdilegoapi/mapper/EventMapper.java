package com.yeoni.birdilegoapi.mapper;

import com.yeoni.birdilegoapi.domain.dto.event.EventDetailDto;
import com.yeoni.birdilegoapi.domain.entity.EventEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface EventMapper {

    void save(EventEntity eventEntity);
    Optional<EventEntity> findById(Long eventId);
    List<EventEntity> findAll(Long creatorId);
    int update(EventEntity eventEntity);
    int deleteById(Long eventId);
    Optional<EventDetailDto> findDetailById(Long eventId);

}
