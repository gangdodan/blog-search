package com.search.common.exception.domain;

import com.search.common.exception.enums.ErrorCode;

public class RedisException extends CustomException {
    public RedisException(ErrorCode errorCode) {
        super(errorCode);
    }
}
