package com.yeoni.birdilegoapi.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class EventFile {

    private Long fileId;
    private Long eventId;
    private String fileType;
    private String originalFileName;
    private String storedFilePath;
    private Long fileSize;
    private Timestamp createdAt;

}
