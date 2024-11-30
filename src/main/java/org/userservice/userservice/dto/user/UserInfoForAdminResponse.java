package org.userservice.userservice.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInfoForAdminResponse {
    @Schema(description = "사용자 아이디", example = "naver_3LSKMZKXkTXpf2D9vEVLK8osUI")
    private String userId;

    @Schema(description = "사용자 닉네임", example = "nickname123")
    private String email;

    @Schema(description = "사용자 프로필 이미지 url", example = "https://objectstorage.kr-central-2.kakaocloud.com/.../example.png")
    private String nickName;

    @Schema(description = "사용자 프로필 이미지 url", example = "http://{storage-hostname}/uploadimage/temp.png")
    private String profileUrl;

    @Schema(description = "상태 메세지", example = "hello!")
    private String profileMessage;

    @Schema(description = "블로그 아이디", example = "1")
    private long blogId;

    @Schema(description = "블로그 소개글", example = "hello!")
    private String blogIntro;
}
