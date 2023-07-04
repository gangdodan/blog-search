package com.common.exception.domain;

import com.common.exception.enums.ErrorCode;
import lombok.Getter;

public class RedisException extends CustomException {
    public RedisException(ErrorCode errorCode) {
        super(errorCode);
    }
}
