package org.userservice.userservice.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.userservice.userservice.domain.CodeLanguage;
import org.userservice.userservice.domain.Tier;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoResponse {

    @Schema(description = "사용자 아이디", example = "naver_3LSKMZKXkTXpf2D9vEVLK8osUI")
    private String userId;

    @Schema(description = "사용자 닉네임", example = "nickname123")
    private String nickName;

    @Schema(description = "사용자 프로필 이미지 url", example = "https://objectstorage.kr-central-2.kakaocloud.com/.../example.png")
    private String profileUrl;

    @Schema(description = "상태 메세지", example = "hello!")
    private String profileMessage;

    @Schema(description = "기본 프로그래밍 언어", example = "java")
    private CodeLanguage codeLanguage;

    @Schema(description = "티어", example = "KING")
    private Tier tier;
}
