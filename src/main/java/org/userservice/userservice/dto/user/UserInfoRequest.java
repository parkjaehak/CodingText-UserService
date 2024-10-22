package org.userservice.userservice.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import org.userservice.userservice.domain.CodeLanguage;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoRequest {

    @Schema(description = "사용자 닉네임", example = "nickname123")
    @Pattern(regexp = "^[^\\s]+$", message = "닉네임에는 공백이 포함될 수 없습니다.")
    private String nickName;

    @Schema(description = "상태 메세지", example = "hello!")
    @Size(max = 30, message = "상태 메세지는 최대 30자여야 합니다.")
    private String profileMessage;

    @Schema(description = "기본 프로그래밍 언어", example = "java")
    private CodeLanguage codeLanguage;
}
