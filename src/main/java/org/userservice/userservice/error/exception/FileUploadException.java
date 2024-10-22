package org.userservice.userservice.error.exception;

import org.userservice.userservice.error.ErrorCode;

public class FileUploadException extends BusinessException{
    public FileUploadException(String message) {
        super(message, ErrorCode.FILE_UPLOAD_FAILED);
    }
}
