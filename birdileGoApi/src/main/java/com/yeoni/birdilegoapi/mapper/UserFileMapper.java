package com.yeoni.birdilegoapi.mapper;

import com.yeoni.birdilegoapi.domain.entity.UserFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserFileMapper {
    void save(UserFile userFile);
    List<UserFile> findByUserId(Long userId);
}
