package org.userservice.userservice.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.userservice.userservice.error.exception.*;

import static org.userservice.userservice.error.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity<ErrorResponse> illegalExHandle(IllegalArgumentException e) {
        final ErrorResponse response = ErrorResponse.of(INVALID_INPUT_VALUE, e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //@Validated 에서 binding error 발생 시 처리
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ErrorResponse> bindExHandle(BindException e) {
        log.error("bind exception: {}", e.getMessage(), e);
        final ErrorResponse response = ErrorResponse.of(INVALID_INPUT_VALUE, e.getBindingResult());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //Http Method 타입이 일치하지 않는 경우
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<?> httpMethodNotSupportedHandle(HttpRequestMethodNotSupportedException e) {
        log.error("HTTP Method Not Supported={}", e.getMessage(), e);
        String errorMessage = "Request method " + e.getMethod() + " is not supported.";
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorMessage);
    }

    // 객체 스토리지관련 에러
    @ExceptionHandler(ImageNotFoundException.class)
    protected ResponseEntity<ErrorResponse> handleImageNotFoundException(ImageNotFoundException e) {
        log.error("Image Not Found: {}", e.getMessage(), e);
        final ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e);
        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }
    @ExceptionHandler(ImageCopyFailedException.class)
    protected ResponseEntity<ErrorResponse> handleImageCopyFailedException(ImageCopyFailedException e) {
        log.error("Image Copy Failed: {}", e.getMessage(), e);
        final ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e);
        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }
    @ExceptionHandler(ImageDeletionFailedException.class)
    protected ResponseEntity<ErrorResponse> handleImageDeletionFailedException(ImageDeletionFailedException e) {
        log.error("Image Deletion Failed: {}", e.getMessage(), e);
        final ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e);
        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

    //토큰 관련 에러
    @ExceptionHandler(RefreshTokenDoesNotMatchException.class)
    protected ResponseEntity<ErrorResponse> refreshExHandle(RefreshTokenDoesNotMatchException e) {
        log.error("Refresh Token Not Found: {}", e.getMessage(), e);
        final ErrorResponse response = ErrorResponse.of(e.getErrorCode(), e);
        return new ResponseEntity<>(response, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }

    //비즈니스 요구사항에 따른 Exception
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> businessExHandle(BusinessException e) {
        log.error("비즈니스 익셉션: {}", e.getMessage(), e);
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse response = ErrorResponse.of(errorCode, e.getErrors());
        return new ResponseEntity<>(response, HttpStatus.valueOf(errorCode.getStatus()));
    }

    //그 밖에 발생하는 모든 예외처리
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> exHandle(Exception e) {
        log.error("Exception: ", e);
        final ErrorResponse response = ErrorResponse.of(INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
