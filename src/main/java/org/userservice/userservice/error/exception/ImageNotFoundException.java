package org.userservice.userservice.error.exception;

import lombok.Getter;
import org.userservice.userservice.error.ErrorCode;

@Getter
public class ImageNotFoundException extends BusinessException {

    public ImageNotFoundException(ErrorCode errorCode, String message) {
        super(message, errorCode);
    }
}
