package org.userservice.userservice.error.exception;

import org.userservice.userservice.error.ErrorCode;

public class UnauthenticatedException extends BusinessException {
    public UnauthenticatedException(String message) {
        super(message, ErrorCode.AUTHENTICATION_NOT_FOUND);
    }
}
