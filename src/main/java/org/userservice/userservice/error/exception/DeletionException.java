package org.userservice.userservice.error.exception;

import org.userservice.userservice.error.ErrorCode;

public class DeletionException extends BusinessException{
    public DeletionException(String message) {
        super(message, ErrorCode.DELETION_FAILED);
    }
}
