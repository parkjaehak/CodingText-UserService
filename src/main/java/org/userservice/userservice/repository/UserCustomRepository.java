package org.userservice.userservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.userservice.userservice.domain.User;
import org.userservice.userservice.dto.user.UserInfoForBlogResponse;

public interface UserCustomRepository {
    UserInfoForBlogResponse findUserInfoForBlogByUserId(String userId);
    Page<User> findAllWithFilters(String input, Pageable pageable);
}
