package org.userservice.userservice.error.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RefreshTokenNotFoundException extends RuntimeException {
    private final HttpStatus status;

    public RefreshTokenNotFoundException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}
