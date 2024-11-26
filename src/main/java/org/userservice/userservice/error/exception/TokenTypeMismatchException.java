package org.userservice.userservice.error.exception;

import org.userservice.userservice.error.ErrorCode;

public class TokenTypeMismatchException extends BusinessException {
    public TokenTypeMismatchException(String message) {
        super(message, ErrorCode.TOKEN_MISMATCH);
    }
}
