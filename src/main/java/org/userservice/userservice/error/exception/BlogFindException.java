package org.userservice.userservice.error.exception;

import org.userservice.userservice.error.ErrorCode;

public class BlogFindException extends BusinessException{
    public BlogFindException(String message) {
        super(message, ErrorCode.BLOG_DELETION_FAILED);
    }
}
