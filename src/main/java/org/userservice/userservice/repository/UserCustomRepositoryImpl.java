package org.userservice.userservice.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.userservice.userservice.domain.QUser;
import org.userservice.userservice.dto.user.UserInfoForBlogResponse;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public UserInfoForBlogResponse findUserInfoForBlogByUserId(String userId) {
        QUser user = QUser.user;

        // QueryDSL을 이용해 필요한 컬럼만 선택적으로 조회
        return queryFactory
                .select(Projections.constructor(UserInfoForBlogResponse.class,
                        user.userId,
                        user.nickName,
                        user.profileUrl,
                        user.profileMessage,
                        user.tier))
                .from(user)
                .where(user.userId.eq(userId)) // userId로 조건을 걸어 조회
                .fetchOne();  // 단일 결과 조회
    }
}
