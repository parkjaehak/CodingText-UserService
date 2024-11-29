package org.userservice.userservice.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.userservice.userservice.domain.QUser;
import org.userservice.userservice.domain.User;
import org.userservice.userservice.dto.user.UserInfoForBlogResponse;

import java.util.List;

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

    @Override
    public Page<User> findAllWithFilters(String input, Pageable pageable) {
        QUser user = QUser.user;

        // 기본 쿼리 작성
        JPQLQuery<User> query = queryFactory.selectFrom(user);

        // 필터 조건 추가 (닉네임 또는 이메일에 유사한 결과)
        if (input != null && !input.isEmpty()) {
            query.where(user.nickName.containsIgnoreCase(input)
                    .or(user.email.containsIgnoreCase(input)));
        }

        // 페이징 처리
        List<User> users = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(user.createdAt.desc())
                .fetch();

        // 전체 개수 조회
        long total = query.fetchCount();

        return new PageImpl<>(users, pageable, total);
    }
}
