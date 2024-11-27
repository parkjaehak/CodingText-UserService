package org.userservice.userservice.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.userservice.userservice.domain.Tier;

@Schema(description = "사용자 문제풀이 통계정보와 관련된 데이터를 전달한다.")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatisticResponse {

    @Schema(description = "사용자 아이디", example = "naver_3LSKMZKXkTXpf2D9vEVLK8osUI")
    private String userId;

    @Schema(description = "사용자 닉네임", example = "nickname123")
    private String nickName;

    @Schema(description = "해결한 문제", example = "100")
    private int solvedCount;

    @Schema(description = "정식 등록된 문제", example = "10")
    private int registerCount;

    @Schema(description = "점수", example = "2500")
    private int totalScore;

    @Schema(description = "등수", example = "5")
    private long rank;

    @Schema(description = "티어", example = "KING")
    private Tier tier;

    @Schema(description = "사용자 프로필 이미지 url", example = "https://objectstorage.kr-central-2.kakaocloud.com/.../example.png")
    private String profileUrl;
}
