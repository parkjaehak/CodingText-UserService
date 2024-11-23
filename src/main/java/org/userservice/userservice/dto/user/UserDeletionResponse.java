package org.userservice.userservice.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDeletionResponse {
    @Schema(description = "사용자 아이디", example = "naver_3LSKMZKXkTXpf2D9vEVLK8osUI")
    private String userId;
    @Schema(description = "사용자 이름", example = "홍길동")
    private String userName;
}