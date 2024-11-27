package org.userservice.userservice.error.exception;

import lombok.Getter;
import org.userservice.userservice.error.ErrorCode;

@Getter
public class RefreshTokenDoesNotMatchException extends RuntimeException {

    private final ErrorCode errorCode;

    public RefreshTokenDoesNotMatchException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
