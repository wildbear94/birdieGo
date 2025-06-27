package com.yeoni.birdilegoapi.exception;

import com.yeoni.birdilegoapi.domain.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 우리가 직접 정의한 CustomException을 처리하는 핸들러
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<CommonResponse<?>> handleCustomException(CustomException e) {
        log.error("handleCustomException: {}", e.getErrorCode());
        ErrorCode errorCode = e.getErrorCode();
        CommonResponse<?> response = CommonResponse.builder()
            .status(errorCode.getStatus().value())
            .code(errorCode.getCode())
            .message(errorCode.getMessage())
            .build();
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    /**
     * 정의하지 않은 나머지 예외들을 처리하는 핸들러
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<CommonResponse<?>> handleException(Exception e) {
        log.error("handleException: {}", e.getMessage());
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        CommonResponse<?> response = CommonResponse.builder()
            .status(errorCode.getStatus().value())
            .code(errorCode.getCode())
            .message(e.getMessage()) // 개발 단계에서는 실제 에러 메시지를 보는 것이 유용할 수 있습니다.
            .build();
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

}
