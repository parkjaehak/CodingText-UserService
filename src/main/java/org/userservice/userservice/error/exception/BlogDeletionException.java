package org.userservice.userservice.error.exception;

import org.userservice.userservice.error.ErrorCode;

public class BlogDeletionException extends BusinessException{
    public BlogDeletionException(String message) {
        super(message, ErrorCode.BLOG_DELETION_FAILED);
    }
}
