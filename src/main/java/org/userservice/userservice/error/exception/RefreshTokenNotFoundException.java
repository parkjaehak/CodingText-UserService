package org.userservice.userservice.error.exception;

import lombok.Getter;
import org.userservice.userservice.error.ErrorCode;

@Getter
public class RefreshTokenNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;

    public RefreshTokenNotFoundException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
