package org.userservice.userservice.error.exception;

import org.userservice.userservice.error.ErrorCode;

public class BlogCreationException extends BusinessException{
    public BlogCreationException(String message) {
        super(message, ErrorCode.BLOG_CREATION_FAILED);
    }
}
