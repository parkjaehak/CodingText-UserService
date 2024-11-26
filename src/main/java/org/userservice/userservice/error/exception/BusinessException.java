package org.userservice.userservice.error.exception;

import lombok.Getter;
import org.userservice.userservice.error.ErrorCode;
import org.userservice.userservice.error.ErrorResponse;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BusinessException extends RuntimeException{

    private final ErrorCode errorCode;
    private List<ErrorResponse.FieldError> errors = new ArrayList<>();

    // 메시지와 함께 ErrorCode 를 받는 생성자
    public BusinessException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    // FieldError 를 포함한 에러를 다룰 수 있는 생성자
    public BusinessException(ErrorCode errorCode, List<ErrorResponse.FieldError> errors) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errors = errors;
    }
}