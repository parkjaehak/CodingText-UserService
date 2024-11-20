package org.userservice.userservice.repository;

import org.userservice.userservice.dto.user.UserInfoForBlogResponse;

public interface UserCustomRepository {
    UserInfoForBlogResponse findUserInfoForBlogByUserId(String userId);
}
