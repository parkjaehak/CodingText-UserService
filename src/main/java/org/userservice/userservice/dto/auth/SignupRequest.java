package org.userservice.userservice.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.userservice.userservice.domain.CodeLanguage;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupRequest {

    @Schema(description = "사용자 닉네임", example = "nickname123")
    private String nickName;

    @Schema(description = "기본 프로그래밍 언어", example = "java")
    private CodeLanguage codeLanguage;

    @Schema(description = "소셜 계정 프로필 사진 사용 여부", example = "true")
    private Boolean useSocialProfile;
}
