package org.userservice.userservice.dto.blogclient;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlogServiceResponse {
    private int statusCode;
    private String message;
    private BlogData data;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class BlogData {
        private long blogId;
        private String mainContent;
    }
}
