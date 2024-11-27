package org.userservice.userservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String REDIS_KEY_USER_SCORES = "user_scores"; // Redis에 저장될 키

    // Redis에서 사용자 점수를 업데이트하는 메서드
    public void updateScore(String userId, int updatedScore) {
        redisTemplate.opsForZSet().add(REDIS_KEY_USER_SCORES, userId, updatedScore);
    }

    //전체 사용자 순위 갱신


    // 사용자 순위 반환 (1위부터 시작)
    public Long getUserRank(String userId) {
        Long rank = redisTemplate.opsForZSet().reverseRank(REDIS_KEY_USER_SCORES, userId);
        return (rank != null) ? rank + 1 : null; // 1부터 시작하는 순위로 변환
    }
}
