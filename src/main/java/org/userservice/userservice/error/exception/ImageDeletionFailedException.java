package org.userservice.userservice.error.exception;

import lombok.Getter;
import org.userservice.userservice.error.ErrorCode;

@Getter
public class ImageDeletionFailedException extends RuntimeException  {
    private final ErrorCode errorCode;

    public ImageDeletionFailedException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
