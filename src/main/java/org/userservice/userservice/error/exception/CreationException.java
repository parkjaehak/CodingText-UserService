package org.userservice.userservice.error.exception;

import org.userservice.userservice.error.ErrorCode;

public class CreationException extends BusinessException{
    public CreationException(String message) {
        super(message, ErrorCode.CREATION_FAILED);
    }
}
