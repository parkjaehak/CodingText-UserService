package org.userservice.userservice.error;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorResponse {

    private int status;
    private String code;
    private String message;
    private List<?> errors;


    private ErrorResponse(final ErrorCode code, final List<FieldError> errors) {
        this.status = code.getStatus();
        this.code = code.getCode();
        this.message = code.getMessage();
        this.errors = errors;
    }
    private ErrorResponse(final ErrorCode code) {
        this.status = code.getStatus();
        this.code = code.getCode();
        this.message = code.getMessage();
        this.errors = new ArrayList<>();
    }
    private ErrorResponse(final ErrorCode code, final String message) {
        this.status = code.getStatus();
        this.code = code.getCode();
        this.message = message;
        this.errors = new ArrayList<>();
    }

    private ErrorResponse(final ErrorCode code, final Throwable throwable) {
        this.status = code.getStatus();
        this.code = code.getCode();
        this.message = code.getMessage();
        this.errors = extractErrors(throwable); // 에러 정보를 추출
    }

    // BindingResult에 대한 ErrorResponse 객체 생성
    public static ErrorResponse of(final ErrorCode code, final BindingResult bindingResult) {
        return new ErrorResponse(code, FieldError.of(bindingResult));
    }
    public static ErrorResponse of(final ErrorCode code, final List<FieldError> errors) {
        return new ErrorResponse(code, errors);
    }
    public static ErrorResponse of(final ErrorCode code) {
        return new ErrorResponse(code);
    }
    public static ErrorResponse of(final ErrorCode code, final String message) {
        return new ErrorResponse(code, message);
    }
    public static ErrorResponse of(final ErrorCode code, final Throwable throwable) {
        return new ErrorResponse(code, throwable);
    }

    // 에러 메시지를 리스트로 추출
    private List<String> extractErrors(Throwable throwable) {
        List<String> errorMessages = new ArrayList<>();
        while (throwable != null) {
            errorMessages.add(throwable.toString()); // 예외 클래스명과 메시지를 포함
            for (StackTraceElement element : throwable.getStackTrace()) {
                errorMessages.add("at " + element.toString()); // 스택 트레이스 정보 추가
            }
            throwable = throwable.getCause(); // 원인 예외 탐색
        }
        return errorMessages;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class FieldError{
        private String field;
        private String value;
        private String reason;

        public static List<FieldError> of(final BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()
                    ))
                    .collect(Collectors.toList());
        }
    }
}