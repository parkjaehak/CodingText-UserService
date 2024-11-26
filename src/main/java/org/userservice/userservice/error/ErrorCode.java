package org.userservice.userservice.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    //프론트에 보여지는 값

    //common
    INTERNAL_SERVER_ERROR(500, "C001", "서버 오류입니다."),
    INVALID_INPUT_VALUE(400, "C002", "잘못된 일력 값을 입력하였습니다."),
    INVALID_TYPE_VALUE(400, "C003", "잘못된 타입을 입력하였습니다."),
    AUTHENTICATION_NOT_FOUND(401, "C004", "인증되지 않은 사용자입니다."),
    NO_AUTHORITY(403, "C005", "권한이 없는 사용자입니다."),
    FILE_UPLOAD_FAILED(500, "C006", "파일 업로드에 실패했습니다."),
    CREATION_FAILED(500, "C007", "생성에 실패하였습니다"),
    TOKEN_MISMATCH(404, "C008", "토큰타입이 불일치합니다."),
    REFRESH_TOKEN_NOT_FOUND(404, "C009", "리프레시 토큰이 없거나 일치하지 않습니다."),

    //user
    USER_NOT_FOUND(404, "U001", "사용자를 찾을 수 없습니다."),

    //feign client
    ANNOUNCEMENT_NOT_FOUNT(404, "F001", "공지사항 정보 찾을 수 없습니다."),

    //object storage
    IMAGE_IO_FAILED(404, "I001", "이미지 버킷 접근 시 I/O 관련 에러가 발생하였습니다."),
    IMAGE_FAILED_ETC(500, "I002", "이미지 버킷 접근 시 기타 에러가 발생하였습니다.");


    private final int status;
    private final String code;
    private final String message;
}
