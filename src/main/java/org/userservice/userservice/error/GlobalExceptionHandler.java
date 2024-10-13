package org.userservice.userservice.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.userservice.userservice.error.exception.BusinessException;

import static org.userservice.userservice.error.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> illegalExHandle(IllegalArgumentException e) {
        final ErrorResponse response = ErrorResponse.of(INVALID_INPUT_VALUE, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //@Validated 에서 binding error 발생 시 처리
    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> bindExHandle(BindException e) {
        final ErrorResponse response = ErrorResponse.of(INVALID_INPUT_VALUE, e.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //비즈니스 요구사항에 따른 Exception
    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> businessExHandle(BusinessException e) {
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse response = ErrorResponse.of(errorCode, e.getErrors());
        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
    }

    //Http Method 타입이 일치하지 않는 경우
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<?> httpMethodNotSupportedHandle(HttpRequestMethodNotSupportedException e) {
        log.error("HTTP Method Not Supported: ", e);
        String errorMessage = "Request method " + e.getMethod() + " is not supported.";
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorMessage);
    }

    //그 밖에 발생하는 모든 예외처리
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> exHandle(Exception e) {
        log.error("Exception: ", e);
        final ErrorResponse response = ErrorResponse.of(INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
