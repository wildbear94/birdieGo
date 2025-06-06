package com.yeoni.birdilegoapi.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {

    private int status;
    private String code;
    private String message;
}
