package org.userservice.userservice.error.exception;

import org.userservice.userservice.error.ErrorCode;

public class ImageCopyFailedException extends BusinessException{
    public ImageCopyFailedException(ErrorCode errorCode, String message) {
        super(message, errorCode);
    }
}
