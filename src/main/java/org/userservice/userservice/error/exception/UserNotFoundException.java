package org.userservice.userservice.error.exception;

import org.userservice.userservice.error.ErrorCode;

public class UserNotFoundException extends BusinessException{
    public UserNotFoundException(String message) {
        super(message, ErrorCode.USER_NOT_FOUND);
    }
}
