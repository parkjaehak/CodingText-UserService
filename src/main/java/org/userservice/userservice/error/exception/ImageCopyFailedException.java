package org.userservice.userservice.error.exception;

import lombok.Getter;
import org.userservice.userservice.error.ErrorCode;

@Getter
public class ImageCopyFailedException extends RuntimeException{
    private final ErrorCode errorCode;
    public ImageCopyFailedException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
