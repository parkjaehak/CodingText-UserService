package org.userservice.userservice.error.exception;

import org.userservice.userservice.error.ErrorCode;

public class UnauthorizedException extends BusinessException{
    public UnauthorizedException(String message) {
        super(message, ErrorCode.NO_AUTHORITY);
    }
}
