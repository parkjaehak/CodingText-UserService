package org.userservice.userservice.dto.adminclient;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnnounceResponse {
    @Schema(description = "공지사항 ID", example = "1")
    private long announceId;

    @Schema(description = "공지사항 제목", example = "2024년 첫 번째 공지")
    private String title;

    @Schema(description = "공지사항 생성 날짜", example = "2024-01-01")
    private LocalDate createdDate;
}