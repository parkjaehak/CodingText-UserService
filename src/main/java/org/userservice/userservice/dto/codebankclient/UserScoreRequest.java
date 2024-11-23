package org.userservice.userservice.dto.codebankclient;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserScoreRequest {
    private String userId;
    private int score;
}
