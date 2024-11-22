package org.userservice.userservice.dto.codebankclient;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPointsResponse {
    private String userId;
    private int points;
}
