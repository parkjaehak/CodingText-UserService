package org.userservice.userservice.error.exception;

import org.userservice.userservice.error.ErrorCode;

public class ImageDeletionFailedException extends BusinessException {
    public ImageDeletionFailedException(ErrorCode errorCode, String message) {
        super(message, errorCode);
    }
}
