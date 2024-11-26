package org.userservice.userservice.error.exception;

import lombok.Getter;
import org.userservice.userservice.error.ErrorCode;
@Getter
public class TokenExpiredException extends RuntimeException{
    private final ErrorCode errorCode;

    public TokenExpiredException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
