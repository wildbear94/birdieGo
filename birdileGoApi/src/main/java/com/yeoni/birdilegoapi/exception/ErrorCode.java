package com.yeoni.birdilegoapi.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "E001", "입력 값이 올바르지 않습니다."),

    // 401 Unauthorized
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "E002", "유효하지 않은 토큰입니다."),
    USER_DISABLED(HttpStatus.UNAUTHORIZED, "E003", "비활성화된 사용자입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "E004", "아이디 또는 비밀번호가 일치하지 않습니다."),


    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "E005", "사용자를 찾을 수 없습니다."),
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "E006", "대회를 찾을 수 없습니다."),

    // 403 Forbidden
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "E007", "해당 리소스에 접근할 권한이 없습니다."),


    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E999", "서버 내부 오류가 발생했습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;

}
