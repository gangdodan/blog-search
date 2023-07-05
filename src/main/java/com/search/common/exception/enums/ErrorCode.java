package com.search.common.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    DATA_NOT_FOUND(NOT_FOUND,"");

    private final HttpStatus status;
    private final String message;

}
