package com.search.common.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    DATA_NOT_FOUND(NOT_FOUND,"데이터를 찾을 수 없습니다."),
    REQUEST_CONFLICT(CONFLICT, "잘못된 요청입니다."),
    UNABLE_TO_PROCESS(SERVICE_UNAVAILABLE, "현재 해당 요청을 수행할 수 없습니다."),

    FAILED_TO_GET_TREND_KEYWORD(CONFLICT, "인기 검색어 조회에 실패했습니다.");


    private final HttpStatus status;
    private final String message;

}
