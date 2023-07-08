package com.search.common.exception.api;

import com.search.common.exception.domain.CustomException;
import com.search.common.exception.dto.ErrorResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<ErrorResponseEntity> handleRuntimeExceptions(RuntimeException e) {
        return ErrorResponseEntity.of(e);
    }

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponseEntity> handleCustomExceptions(CustomException e) {
        return ErrorResponseEntity.of(e);
    }

}
