package org.userservice.userservice.dto.user;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStatisticResponse {
    private String userId;
    private String nickName;
    private int totalScore;
    private int registerCount;
    private int solvedCount;
    private int rank;
}
