package com.common.exception.dto;

import com.common.exception.domain.CustomException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public final class ErrorResponseEntity {
    @Getter
    private final int status;
    @Getter
    private final String code;
    @Getter
    private final String message;

    private ErrorResponseEntity(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public static ResponseEntity<ErrorResponseEntity> of(RuntimeException e) {
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseEntity(INTERNAL_SERVER_ERROR.value(), INTERNAL_SERVER_ERROR.toString(), e.getMessage()));
    }

    public static ResponseEntity<ErrorResponseEntity> of(CustomException e) {
        HttpStatus status = e.getErrorCode().getStatus();
        String message = (e.getMessage() == null) ? e.getErrorCode().getMessage() : e.getMessage();

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(new ErrorResponseEntity(status.value(), status.toString(), message));
    }


}
