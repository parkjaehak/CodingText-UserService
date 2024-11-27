package org.userservice.userservice.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.userservice.userservice.domain.CodeLanguage;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupRequest {

    @Schema(description = "사용자 닉네임", example = "nickname123")
    @NotBlank(message = "닉네임은 필수입니다.")
    @Pattern(regexp = "^[^\\s]+$", message = "닉네임에는 공백이 포함될 수 없습니다.")
    private String nickName;

    @Schema(description = "기본 프로그래밍 언어", example = "java")
    @NotNull(message = "기본 프로그래밍 언어는 필수입니다.")
    private CodeLanguage codeLanguage;

    @Schema(description = "소셜 계정 프로필 사진 사용 여부", example = "true")
    @NotNull(message = "소셜 계정 프로필 사진 사용 여부는 필수입니다.")
    private Boolean useSocialProfile;

    @Schema(description = "기본 프로필 사진 경로", example = "/profileImg1.png")
    private String basicProfileUrl;
}
