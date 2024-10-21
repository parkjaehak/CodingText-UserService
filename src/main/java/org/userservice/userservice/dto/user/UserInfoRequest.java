package org.userservice.userservice.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import org.userservice.userservice.domain.CodeLanguage;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoRequest {

    @Schema(description = "사용자 닉네임", example = "nickname123")
    private String nickName;

    @Schema(description = "상태 메세지", example = "hello!")
    private String profileMessage;

    @Schema(description = "기본 프로그래밍 언어", example = "java")
    private CodeLanguage codeLanguage;
}
